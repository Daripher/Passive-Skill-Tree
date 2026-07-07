package daripher.skilltree.inventory.menu;

import daripher.skilltree.init.PSTBlocks;
import daripher.skilltree.init.PSTMenuTypes;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.inventory.slot.WorkbenchBaseSlot;
import daripher.skilltree.inventory.slot.WorkbenchResultSlot;
import daripher.skilltree.inventory.slot.WorkbenchIngredientSlot;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaCraftingRecipe;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.CraftedItemBonusBonus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorkbenchMenu extends AbstractContainerMenu {
    private static final List<AbstractWorkbenchRecipe> WORKBENCH_RECIPE_CACHE = new ArrayList<>();
    private static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = RESULT_SLOT + 1;
    private static final int CRAFT_SLOT_END = CRAFT_SLOT_START + 10;
    private static final int INV_SLOT_START = CRAFT_SLOT_END;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int HOTBAR_SLOT_START = INV_SLOT_END;
    private static final int HOTBAR_SLOT_END = HOTBAR_SLOT_START + 9;
    private final WorkbenchContainer workbenchContainer;
    private final ResultContainer resultSlots;
    private final ContainerLevelAccess levelAccess;
    private final Player player;
    private final DataSlot selectedRecipeIndex;
    private List<AbstractWorkbenchRecipe> selectedRecipes = new ArrayList<>();
    private @NotNull ItemStack prevInput = ItemStack.EMPTY;
    private final Level level;
    private @Nullable Runnable recipeListUpdateListener;

    public WorkbenchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public WorkbenchMenu(int containerId, Inventory playerInventory, ContainerLevelAccess levelAccess) {
        super(PSTMenuTypes.ARTISAN_WORKBENCH.get(), containerId);
        this.selectedRecipeIndex = DataSlot.standalone();
        this.workbenchContainer = new WorkbenchContainer(this);
        this.resultSlots = new ResultContainer();
        this.levelAccess = levelAccess;
        this.player = playerInventory.player;
        this.level = player.level();
        addSlot(new WorkbenchResultSlot(playerInventory.player, workbenchContainer, resultSlots, 0, 143, 129));
        addSlot(new WorkbenchBaseSlot(workbenchContainer, 0, 8, 120));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (i == 0 && j == 0) {
                    continue;
                }
                addSlot(new WorkbenchIngredientSlot(workbenchContainer, j + i * 5, 8 + j * 18, 120 + i * 18, j + i * 5 - 1));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 218));
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 160 + i * 18));
            }
        }
        addDataSlot(selectedRecipeIndex);
        setupRecipeList();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack movedStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return movedStack;
        }
        ItemStack clickedStack = slot.getItem();
        movedStack = clickedStack.copy();
        if (slotIndex == RESULT_SLOT) {
            levelAccess.execute((level, blockPos) -> clickedStack.getItem().onCraftedBy(clickedStack, level, player));
            if (!moveItemStackTo(clickedStack, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(clickedStack, movedStack);
        } else if (slotIndex >= INV_SLOT_START && slotIndex < HOTBAR_SLOT_END) {
            if (!moveItemStackTo(clickedStack, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                if (slotIndex < INV_SLOT_END) {
                    if (!moveItemStackTo(clickedStack, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(clickedStack, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!moveItemStackTo(clickedStack, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
            return ItemStack.EMPTY;
        }
        if (clickedStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (clickedStack.getCount() == movedStack.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, clickedStack);
        if (slotIndex == 0) {
            player.drop(clickedStack, false);
        }
        return movedStack;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        levelAccess.execute((level, blockPos) -> clearContainer(player, workbenchContainer));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, PSTBlocks.WORKBENCH.get());
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id == -1) {
            selectedRecipeIndex.set(id);
            setupRecipeList();
        }
        if (isValidRecipeIndex(id)) {
            selectedRecipeIndex.set(id);
            AbstractWorkbenchRecipe selectedRecipe = getSelectedRecipe();
            if (selectedRecipe != null) {
                updateCraftingResult(selectedRecipe);
            }
        }
        return true;
    }

    private boolean isValidRecipeIndex(int recipeIndex) {
        return recipeIndex >= 0 && recipeIndex < selectedRecipes.size();
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        updateSelectedRecipe();
    }

    private void updateSelectedRecipe() {
        ItemStack input = workbenchContainer.getBaseItem();
        AbstractWorkbenchRecipe selectedRecipe = getSelectedRecipe();
        if (selectedRecipe != null) {
            updateCraftingResult(selectedRecipe);
            return;
        }
        if (!ItemStack.isSameItemSameTags(input, prevInput)) {
            setupRecipeList();
            prevInput = input.copy();
        }
        broadcastChanges();
        if (recipeListUpdateListener != null) {
            recipeListUpdateListener.run();
        }
    }

    public void setRecipeListUpdateListener(@Nullable Runnable recipeListUpdateListener) {
        this.recipeListUpdateListener = recipeListUpdateListener;
    }

    private void updateCraftingResult(AbstractWorkbenchRecipe selectedRecipe) {
        if (!selectedRecipe.matches(workbenchContainer, level)) {
            resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            if (!level.isClientSide) {
                ItemStack craftResult = selectedRecipe.assemble(workbenchContainer, level.registryAccess());
                addCraftingBonuses(craftResult);
                resultSlots.setRecipeUsed(selectedRecipe);
                resultSlots.setItem(0, craftResult);
            }
        }
    }

    public void addCraftingBonuses(ItemStack craftResult) {
        SkillBonusProvider.getMergedSkillBonuses(player, CraftedItemBonusBonus.class).forEach(bonus -> bonus.itemCrafted(craftResult));
    }

    private void setupRecipeList() {
        selectedRecipeIndex.set(-1);
        resultSlots.setItem(0, ItemStack.EMPTY);
        selectedRecipes = getAllWorkbenchRecipes().stream().filter(this::shouldDisplayRecipe)
                .sorted(Comparator.comparing(AbstractWorkbenchRecipe::getId)).toList();
    }

    private List<AbstractWorkbenchRecipe> getAllWorkbenchRecipes() {
        if (!WORKBENCH_RECIPE_CACHE.isEmpty()) {
            return WORKBENCH_RECIPE_CACHE;
        }
        List<CraftingRecipe> vanillaCraftingRecipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(recipe -> !recipe.getResultItem(level.registryAccess()).isEmpty()).toList();
        WORKBENCH_RECIPE_CACHE.addAll(level.getRecipeManager().getAllRecipesFor(PSTRecipeTypes.WORKBENCH));
        WORKBENCH_RECIPE_CACHE.addAll(vanillaCraftingRecipes.stream().map(this::convertVanillaRecipe).toList());
        return WORKBENCH_RECIPE_CACHE;
    }

    private AbstractWorkbenchRecipe convertVanillaRecipe(CraftingRecipe craftingRecipe) {
        return new WorkbenchVanillaCraftingRecipe(craftingRecipe, level.registryAccess());
    }

    private boolean shouldDisplayRecipe(AbstractWorkbenchRecipe recipe) {
        if (recipe.isLockedFor(player)) {
            return false;
        }
        return workbenchContainer.getBaseItem().isEmpty() || recipe.isValidBaseItem(workbenchContainer.getBaseItem());
    }

    public Player getPlayer() {
        return player;
    }

    public List<AbstractWorkbenchRecipe> getSelectedRecipes() {
        return selectedRecipes;
    }

    public int getSelectedRecipeIndex() {
        return selectedRecipeIndex.get();
    }

    public WorkbenchContainer getWorkbenchContainer() {
        return workbenchContainer;
    }

    public ItemStack getResultItem() {
        return resultSlots.getItem(0);
    }

    public @Nullable AbstractWorkbenchRecipe getSelectedRecipe() {
        if (selectedRecipes.isEmpty()) {
            return null;
        }
        int index = selectedRecipeIndex.get();
        if (index >= selectedRecipes.size() || index < 0) {
            return null;
        }
        return selectedRecipes.get(index);
    }
}
