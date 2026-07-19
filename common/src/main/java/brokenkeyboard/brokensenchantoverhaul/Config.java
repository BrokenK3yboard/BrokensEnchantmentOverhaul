package brokenkeyboard.brokensenchantoverhaul;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {

    public static final ModConfigSpec SPEC;
    public static ModConfigSpec.BooleanValue OVERHAUL_ENCHANTMENTS;
    public static ModConfigSpec.IntValue ENCHANTMENT_COST;
    public static ModConfigSpec.IntValue ENCHANTED_BOOK_ANVIL_COST;
    public static ModConfigSpec.IntValue ENCHANTED_ITEM_MATERIAL_COST;
    public static ModConfigSpec.IntValue ENCHANTED_ITEM_FULL_COST;
    public static ModConfigSpec.IntValue ANVIL_RENAME_COST;
    public static ModConfigSpec.BooleanValue TRIDENT_BUILTIN_LOYALTY;
    public static ModConfigSpec.ConfigValue<List<String>> TIER_ENCHANTABILITY_OVERRIDE;
    public static ModConfigSpec.ConfigValue<List<String>> ARMOR_ENCHANTABILITY_OVERRIDE;

    static {
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        registerConfig(configBuilder);
        SPEC = configBuilder.build();
    }

    public static void registerConfig(ModConfigSpec.Builder builder) {

        OVERHAUL_ENCHANTMENTS = builder
                .comment("If enabled: ")
                .comment("1. All items can only have one enchantment")
                .comment("2. Enchantments will only have one level")
                .comment("3. Enchantments will function as if they are at their original maximum level")
                .comment("4. Enchanted armor, tools, weapons gain increases to their protection, efficiency, durability respectively")
                .comment("5. Removes anvil work costs; repair costs are fixed amounts depending on whether an item is enchanted or not\n")
                .comment("Disabling this is not recommended and will have negative effects on gameplay unless you are a datapack author who knows what you're doing.")
                .define("Single enchantment mode", true);

        ENCHANTMENT_COST = builder
                .comment("The experience and lapis cost to apply an enchantment to an item in an enchanting table")
                .defineInRange("Enchantment cost", 10, 4, 64);

        ENCHANTED_BOOK_ANVIL_COST = builder
                .comment("The experience needed to apply an enchanted book to an item in an anvil")
                .defineInRange("Enchanted book anvil cost", 10, 4, 64);

        ENCHANTED_ITEM_MATERIAL_COST = builder
                .comment("The experience needed to repair enchanted items in an anvil using its repair material")
                .defineInRange("Enchanted item material repair cost", 2, 2, 64);

        ENCHANTED_ITEM_FULL_COST = builder
                .comment("The experience needed to repair enchanted items in an anvil using a copy of the item")
                .defineInRange("Enchanted item full repair cost", 5, 4, 64);

        ANVIL_RENAME_COST = builder
                .comment("The experience needed to rename items in an anvil")
                .defineInRange("Anvil rename cost", 1, 1, 64);

        TRIDENT_BUILTIN_LOYALTY = builder
                .comment("When enabled, thrown tridents will return to their owners without the need for the Loyalty Enchantment")
                .define("Tridents have builtin Loyalty", true);

        TIER_ENCHANTABILITY_OVERRIDE = builder
                .comment("Overrides the enchantment value of tool and weapon material tiers. Format is tier=value. Minimum value is 1, maximum value is 25.")
                .define("Tiered item enchantment value overrides", List.of());

        ARMOR_ENCHANTABILITY_OVERRIDE = builder
                .comment("Overrides the enchantment value of armor materials. Format is namespace:material=value. Minimum value is 1, maximum value is 25.")
                .define("Armor enchantment value overrides", List.of("minecraft:leather=21", "minecraft:chainmail=18", "minecraft:iron=15", "minecraft:netherite=10"));
    }
}
