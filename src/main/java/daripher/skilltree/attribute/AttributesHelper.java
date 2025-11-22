package daripher.skilltree.attribute;

import java.util.Collection;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributesHelper {
  public static Collection<Attribute> attributeList() {
    //noinspection deprecation
    return ForgeRegistries.ATTRIBUTES.getValues().stream()
        .filter(ForgeHooks.getAttributesView().get(EntityType.PLAYER)::hasAttribute)
        .toList();
  }

  public static String getName(Attribute attribute) {
    ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attribute);
    Objects.requireNonNull(id);
    return id.toString();
  }
}
