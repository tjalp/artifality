package artifality.item;

import artifality.interfaces.IArtifalityBlock;
import artifality.util.TooltipUtils;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseBlockItem extends BlockItem {


    public BaseBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public @Nullable String getDescription(){
        if(getBlock() instanceof IArtifalityBlock){
            return ((IArtifalityBlock) getBlock()).getDescription();
        }else return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        TooltipUtils.appendDescription(stack, tooltip);
    }
}
