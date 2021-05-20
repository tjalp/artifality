package artifality;

import artifality.data.ArtifalityResources;
import artifality.data.ArtifalityLootTables;
import artifality.enchantment.ArtifalityEnchantments;
import artifality.event.ArtifalityEvents;
import artifality.item.ArtifalityItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class ArtifalityMod implements ModInitializer {

    public static final String MODID = "artifality";

    public static final ItemGroup GENERAL = FabricItemGroupBuilder.create(new Identifier(MODID, "general")).appendItems(new Consumer<List<ItemStack>>() {
        @Override
        public void accept(List<ItemStack> itemStacks) {

            ArtifalityItems.getItems().forEach(((id, item) -> {
                if(item.getGroup().equals(GENERAL)){
                    itemStacks.add(item.getDefaultStack());
                }
            }));

            ArtifalityEnchantments.getEnchantments().forEach(((id, enchantment) -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                book.addEnchantment(enchantment, enchantment.getMaxLevel());
                itemStacks.add(book);
            }));

        }
    }).icon(new Supplier<ItemStack>() {
        @Override
        public ItemStack get() {
            return ArtifalityItems.UKULELE.getDefaultStack();
        }
    }).build();


    @Override
    public void onInitialize() {

        ArtifalityItems.register();
        ArtifalityEvents.register();
        ArtifalityEnchantments.register();
        ArtifalityResources.init();
        ArtifalityLootTables.init();
    }
}
