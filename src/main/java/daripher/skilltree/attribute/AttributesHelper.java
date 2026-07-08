package daripher.skilltree.attribute;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;

public class AttributesHelper {
    public static Collection<Attribute> playerAttributesList() {
        if (!DefaultAttributes.hasSupplier(EntityType.PLAYER)) {
            SkillTreeMod.LOGGER.error("Can not find player attribute supplier!");
            return List.of();
        }
        AttributeSupplier attributeSupplier = DefaultAttributes.getSupplier(EntityType.PLAYER);
        return ForgeRegistries.ATTRIBUTES.getValues().stream().filter(attributeSupplier::hasAttribute).toList();
    }

    public static String getName(Attribute attribute) {
        ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attribute);
        if (id == null) {
            SkillTreeMod.LOGGER.warn("Unregistered attribute: {}", attribute);
            return "unknown:unregistered_attribute";
        }
        return id.toString();
    }
}
