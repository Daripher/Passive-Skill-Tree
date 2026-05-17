package daripher.skilltree.attribute;

import java.util.Collection;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import daripher.skilltree.util.ForgeRegistries;

public class AttributesHelper {
  public static Collection<Attribute> attributeList() {
    //noinspection deprecation
    return ForgeRegistries.ATTRIBUTES.getValues().stream()
        .filter(attribute -> Player.createAttributes().build().hasAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute)))
        .toList();
  }

  public static String getName(Attribute attribute) {
    ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attribute);
    Objects.requireNonNull(id);
    return id.toString();
  }
}
