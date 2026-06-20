package daripher.skilltree.client.screen.menu;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.inventory.menu.WorkbenchMenu;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkbenchScreen extends AbstractContainerScreen<WorkbenchMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "textures/gui/container/workbench.png");
    private static final ResourceLocation RECIPES_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "textures/gui/container/workbench_recipes.png");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_FULL_HEIGHT = 90;
    private static final int RECIPES_X = 8;
    private static final int RECIPES_Y = 24;
    private static final int RECIPE_WIDTH = 143;
    private static final int RECIPE_HEIGHT = 18;
    private final List<Pair<AbstractWorkbenchRecipe, Integer>> searchedRecipes = new ArrayList<>();
    private EditBox searchBox;
    private int amountScrolled;
    private float tickCount;

    public WorkbenchScreen(WorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight = 242;
        menu.setRecipeListUpdateListener(this::refreshSearchResults);
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();
        searchBox = new EditBox(font, leftPos + 36, topPos + 9, 102, 10, Component.empty());
        searchBox.setMaxLength(57);
        searchBox.setBordered(false);
        searchBox.setTextColor(0xffffff);
        addRenderableWidget(searchBox);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String search = searchBox.getValue();
        init(minecraft, width, height);
        searchBox.setValue(search);
        if (!searchBox.getValue().isEmpty()) {
            refreshSearchResults();
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        renderBackground(guiGraphics);
        guiGraphics.blit(BACKGROUND_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderScroll(guiGraphics);
        renderRecipes(guiGraphics, mouseX, mouseY);
        if (searchBox.getValue().isEmpty()) {
            Component searchHint = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC);
            guiGraphics.drawString(font, searchHint, searchBox.getX(), searchBox.getY(), 0x555555, false);
        }
    }

    private void renderScroll(@NotNull GuiGraphics guiGraphics) {
        int scrollerIconIndex = (isScrollBarActive() ? 2 : 1);
        int scrollerX = leftPos + 156;
        float scrollOffset = (float) amountScrolled / getMaxScroll();
        int scrollerY = (int) (topPos + 24 + (SCROLLER_FULL_HEIGHT - SCROLLER_HEIGHT) * scrollOffset);
        guiGraphics.blit(BACKGROUND_TEXTURE, scrollerX, scrollerY, -SCROLLER_WIDTH * scrollerIconIndex, 0, SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    private void renderRecipes(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int x = leftPos + RECIPES_X;
        for (int i = 0; i < Math.min(5, searchedRecipes.size()); i++) {
            int recipeIndex = getRecipeInSlot(i).getValue();
            int y = topPos + RECIPES_Y + i * RECIPE_HEIGHT;
            int recipeTexture = getRecipeTexture(mouseX, mouseY, recipeIndex, i);
            int vOffset = recipeTexture * RECIPE_HEIGHT;
            guiGraphics.blit(RECIPES_TEXTURE, x, y, 0, vOffset, RECIPE_WIDTH, RECIPE_HEIGHT);
            AbstractWorkbenchRecipe recipe = getRecipeInSlot(i).getKey();
            String tooltip = recipe.getShortDescription().getString();
            tooltip = TooltipHelper.getTrimmedString(font, tooltip, RECIPE_WIDTH - 4);
            guiGraphics.drawString(font, tooltip, x + 2, y + 5, 0xffffff);
        }
    }

    private int getRecipeTexture(double mouseX, double mouseY, int recipeIndex, int recipeSlot) {
        if (menu.getSelectedRecipeIndex() == recipeIndex) {
            return 1;
        }
        if (isMouseOverRecipe(recipeSlot, mouseX, mouseY)) {
            return 2;
        }
        return 0;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderGhostRecipe(guiGraphics);
        renderTooltip(guiGraphics, mouseX, mouseY);
        tickCount += partialTicks;
    }

    private void renderGhostRecipe(GuiGraphics guiGraphics) {
        AbstractWorkbenchRecipe selectedRecipe = menu.getSelectedRecipe();
        if (selectedRecipe == null) {
            for (int i = 1; i < 10; i++) {
                int itemX = leftPos + 8 + i % 5 * 18;
                int itemY = topPos + 120 + i / 5 * 18;
                renderMissingItemOverlay(guiGraphics, itemX, itemY);
            }
            return;
        }
        renderGhostBaseIngredient(guiGraphics, selectedRecipe);
        renderGhostAdditionalIngredients(guiGraphics, selectedRecipe);
        renderGhostResult(guiGraphics, selectedRecipe);
    }

    private void renderGhostResult(GuiGraphics guiGraphics, AbstractWorkbenchRecipe selectedRecipe) {
        if (!menu.getResultItem().isEmpty()) {
            return;
        }
        renderMissingItemOverlay(guiGraphics, leftPos + 134, topPos + 120, 34);
        renderMissingItemStack(guiGraphics, leftPos + 143, topPos + 129, getResultItem(selectedRecipe));
    }

    private void renderGhostAdditionalIngredients(GuiGraphics guiGraphics, AbstractWorkbenchRecipe selectedRecipe) {
        ItemStack baseItemStack = menu.getWorkbenchContainer().getBaseItem();
        Map<Ingredient, Integer> additionalIngredients = selectedRecipe.getAdditionalIngredients(baseItemStack);
        List<Map.Entry<Ingredient, Integer>> requiredIngredients = additionalIngredients.entrySet().stream().toList();
        for (int ingredientIndex = 0; ingredientIndex < 9; ingredientIndex++) {
            int slot = ingredientIndex + 1;
            int itemX = leftPos + 8 + (slot % 5) * 18;
            int itemY = topPos + 120 + (slot / 5) * 18;
            if (ingredientIndex >= requiredIngredients.size()) {
                renderMissingItemOverlay(guiGraphics, itemX, itemY);
                continue;
            }
            ItemStack existingIngredient = menu.getWorkbenchContainer().getItem(slot);
            Map.Entry<Ingredient, Integer> ingredientAmountEntry = requiredIngredients.get(ingredientIndex);
            if (existingIngredient.isEmpty()) {
                renderMissingIngredient(guiGraphics, slot, itemX, itemY, Pair.of(ingredientAmountEntry));
                continue;
            }
            int requiredAmount = ingredientAmountEntry.getValue();
            if (existingIngredient.getCount() < requiredAmount) {
                renderMissingItemOverlay(guiGraphics, itemX, itemY);
            }
        }
    }

    private void renderGhostBaseIngredient(GuiGraphics guiGraphics, AbstractWorkbenchRecipe selectedRecipe) {
        int requiredBaseItemAmount = selectedRecipe.requiredBaseItemAmount();
        Pair<Ingredient, Integer> baseIngredient = selectedRecipe.getBaseIngredient();
        boolean usingValidBaseItem = selectedRecipe.isValidBaseItem(menu.getWorkbenchContainer().getBaseItem());
        if (requiredBaseItemAmount == 0 && !menu.getWorkbenchContainer().getBaseItem().isEmpty()) {
            renderMissingItemOverlay(guiGraphics, leftPos + 8, topPos + 120);
        }
        if (baseIngredient != null && requiredBaseItemAmount > 0 && !usingValidBaseItem) {
            renderMissingIngredient(guiGraphics, 0, leftPos + 8, topPos + 120, baseIngredient);
        }
    }

    private void renderMissingIngredient(GuiGraphics guiGraphics, int slot, int x, int y, Pair<Ingredient, Integer> ingredientAmountPair) {
        renderMissingItemOverlay(guiGraphics, x, y);
        ItemStack itemStack = getDisplayedItemStack(ingredientAmountPair, slot);
        itemStack.setCount(ingredientAmountPair.getRight());
        if (!itemStack.isEmpty()) {
            renderMissingItemStack(guiGraphics, x, y, itemStack);
        }
    }

    private ItemStack getDisplayedItemStack(Pair<Ingredient, Integer> ingredientAmountPair, int slot) {
        ItemStack[] ingredientItemStacks = ingredientAmountPair.getLeft().getItems();
        Random random = new Random(slot);
        int ingredientCount = ingredientItemStacks.length;
        int displayedItemIndex = Mth.floor((tickCount / 20f + random.nextInt(ingredientCount)) % ingredientCount);
        return ingredientItemStacks[displayedItemIndex];
    }

    private void renderMissingItemOverlay(GuiGraphics guiGraphics, int x, int y) {
        renderMissingItemOverlay(guiGraphics, x, y, 16);
    }

    private void renderMissingItemOverlay(GuiGraphics guiGraphics, int x, int y, int slotSize) {
        guiGraphics.fill(x, y, x + slotSize, y + slotSize, 0x30ff0000);
    }

    private @NotNull ItemStack getResultItem(AbstractWorkbenchRecipe selectedRecipe) {
        ItemStack resultItem = selectedRecipe.getResult(menu.getWorkbenchContainer());
        menu.addCraftingBonuses(resultItem);
        return resultItem;
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        renderRecipesTooltip(guiGraphics, mouseX, mouseY);
        renderGhostRecipeTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderGhostRecipeTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int selectedRecipeIndex = menu.getSelectedRecipeIndex();
        if (selectedRecipeIndex <= -1) {
            return;
        }
        List<AbstractWorkbenchRecipe> selectedRecipes = menu.getSelectedRecipes();
        if (selectedRecipes.isEmpty()) {
            return;
        }
        AbstractWorkbenchRecipe selectedRecipe = selectedRecipes.get(selectedRecipeIndex);
        int requiredBaseItemAmount = selectedRecipe.requiredBaseItemAmount();
        Pair<Ingredient, Integer> requiredBaseItem = selectedRecipe.getBaseIngredient();
        boolean missingBaseItem = menu.getWorkbenchContainer().getBaseItem().isEmpty();
        if (requiredBaseItem != null && requiredBaseItemAmount > 0 && missingBaseItem) {
            int itemX = leftPos + 8;
            int itemY = topPos + 120;
            if (isMouseOverArea(mouseX, mouseY, itemX, itemY, 16, 16)) {
                ItemStack requiredBaseItemStack = getDisplayedItemStack(requiredBaseItem, 0);
                if (!requiredBaseItemStack.isEmpty() && menu.getWorkbenchContainer().getBaseItem().isEmpty()) {
                    renderItemTooltip(guiGraphics, mouseX, mouseY, requiredBaseItemStack);
                }
            }
        }
        AtomicInteger slotIndex = new AtomicInteger(1);
        ItemStack baseItemStack = menu.getWorkbenchContainer().getBaseItem();
        Map<Ingredient, Integer> additionalIngredients = selectedRecipe.getAdditionalIngredients(baseItemStack);
        additionalIngredients.entrySet().forEach(ingredientAmountEntry -> {
            int slot = slotIndex.get();
            if (menu.getWorkbenchContainer().getItem(slot).isEmpty()) {
                int itemX = leftPos + 8 + slot % 5 * 18;
                int itemY = topPos + 120 + slot / 5 * 18;
                if (isMouseOverArea(mouseX, mouseY, itemX, itemY, 16, 16)) {
                    Pair<Ingredient, Integer> ingredientAmountPair = Pair.of(ingredientAmountEntry);
                    renderItemTooltip(guiGraphics, mouseX, mouseY, getDisplayedItemStack(ingredientAmountPair, slot));
                }
                slotIndex.getAndIncrement();
            }
        });
        if (menu.getResultItem().isEmpty() && isMouseOverArea(mouseX, mouseY, leftPos + 134, topPos + 120, 34, 34)) {
            Objects.requireNonNull(minecraft);
            ItemStack resultItem = getResultItem(selectedRecipe);
            renderItemTooltip(guiGraphics, mouseX, mouseY, resultItem);
        }
    }

    private void renderRecipesTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (int i = 0; i < Math.min(5, searchedRecipes.size()); i++) {
            if (!isMouseOverRecipe(i, mouseX, mouseY)) {
                continue;
            }
            AbstractWorkbenchRecipe recipe = getRecipeInSlot(i).getKey();
            guiGraphics.renderComponentTooltip(font, recipe.getFullDescription(), mouseX, mouseY);
        }
    }

    private void renderItemTooltip(@NotNull GuiGraphics guiGraphics, int x, int y, ItemStack itemStack) {
        List<Component> tooltip = getTooltipFromContainerItem(itemStack);
        Optional<TooltipComponent> tooltipImage = itemStack.getTooltipImage();
        guiGraphics.renderTooltip(font, tooltip, tooltipImage, itemStack, x, y);
    }

    private void renderMissingItemStack(GuiGraphics guiGraphics, int itemX, int itemY, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        guiGraphics.renderFakeItem(itemStack, itemX, itemY);
        guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), itemX, itemY, itemX + 16, itemY + 16, 0x30ffffff);
        guiGraphics.renderItemDecorations(font, itemStack, itemX, itemY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        searchBox.tick();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        String search = searchBox.getValue();
        if (searchBox.charTyped(codePoint, modifiers)) {
            if (!Objects.equals(search, searchBox.getValue())) {
                refreshSearchResults();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && menu.getSelectedRecipe() != null) {
            selectRecipe(-1);
            return true;
        }
        String search = searchBox.getValue();
        if (searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            if (!Objects.equals(search, searchBox.getValue())) {
                refreshSearchResults();
            }
            return true;
        } else {
            return searchBox.isFocused() && searchBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE || super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private boolean isScrollBarActive() {
        return searchedRecipes.size() > 5;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchBox.mouseClicked(mouseX, mouseY, button)) {
            searchBox.setFocused(true);
            return true;
        }
        searchBox.setFocused(false);
        Objects.requireNonNull(minecraft);
        LocalPlayer player = minecraft.player;
        Objects.requireNonNull(player);
        for (int i = 0; i < Math.min(5, searchedRecipes.size()); i++) {
            int recipeIndex = getRecipeInSlot(i).getValue();
            if (isMouseOverRecipe(i, mouseX, mouseY) && menu.clickMenuButton(player, recipeIndex)) {
                selectRecipe(recipeIndex);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isScrollBarActive()) {
            amountScrolled = (int) Mth.clamp(amountScrolled - delta, 0, getMaxScroll());
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private Pair<AbstractWorkbenchRecipe, Integer> getRecipeInSlot(int slot) {
        if (slot + amountScrolled >= searchedRecipes.size()) {
            return searchedRecipes.get(0);
        }
        return searchedRecipes.get(slot + amountScrolled);
    }

    private int getMaxScroll() {
        return searchedRecipes.size() - 5;
    }

    private void refreshSearchResults() {
        List<AbstractWorkbenchRecipe> selectedRecipes = menu.getSelectedRecipes();
        searchedRecipes.clear();
        for (int i = 0; i < selectedRecipes.size(); i++) {
            AbstractWorkbenchRecipe recipe = selectedRecipes.get(i);
            String search = searchBox.getValue();
            String recipeTitle = recipe.getShortDescription().getString().toLowerCase(Locale.ROOT);
            if (search.isEmpty() || recipeTitle.contains(search.toLowerCase(Locale.ROOT))) {
                searchedRecipes.add(Pair.of(recipe, i));
            }
        }
    }

    private void selectRecipe(int index) {
        Objects.requireNonNull(minecraft);
        SoundManager soundManager = minecraft.getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
        MultiPlayerGameMode gameMode = minecraft.gameMode;
        Objects.requireNonNull(gameMode);
        gameMode.handleInventoryButtonClick(menu.containerId, index);
    }

    private boolean isMouseOverRecipe(int recipeIndex, double mouseX, double mouseY) {
        int recipeX = leftPos + RECIPES_X;
        int recipeY = topPos + RECIPES_Y + recipeIndex * RECIPE_HEIGHT;
        return mouseX >= recipeX && mouseY >= recipeY && mouseX < recipeX + RECIPE_WIDTH && mouseY < recipeY + RECIPE_HEIGHT;
    }

    private boolean isMouseOverArea(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
