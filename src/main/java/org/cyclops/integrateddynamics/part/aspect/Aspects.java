package org.cyclops.integrateddynamics.part.aspect;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectRead;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectRegistry;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.part.aspect.build.IAspectValuePropagator;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integrateddynamics.part.aspect.read.fluid.*;
import org.cyclops.integrateddynamics.part.aspect.read.minecraft.AspectReadIntegerMinecraftPlayerCount;
import org.cyclops.integrateddynamics.part.aspect.read.minecraft.AspectReadIntegerMinecraftRandom;
import org.cyclops.integrateddynamics.part.aspect.read.minecraft.AspectReadIntegerMinecraftTicktime;
import org.cyclops.integrateddynamics.part.aspect.read.network.*;
import org.cyclops.integrateddynamics.part.aspect.write.AspectWriteBooleanRedstone;
import org.cyclops.integrateddynamics.part.aspect.write.AspectWriteIntegerRedstone;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Collection of all aspects.
 * @author rubensworks
 */
public class Aspects {

    public static final IAspectRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(IAspectRegistry.class);

    public static void load() {}

    public static final class Read {

        public static final class Redstone {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_LOW =
                    AspectReadBuilders.Redstone.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<Integer, Boolean>() {
                        @Override
                        public Boolean getOutput(Integer input) {
                            return input == 0;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "low").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_NONLOW =
                    AspectReadBuilders.Redstone.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<Integer, Boolean>() {
                        @Override
                        public Boolean getOutput(Integer input) {
                            return input > 0;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "nonlow").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_HIGH =
                    AspectReadBuilders.Redstone.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<Integer, Boolean>() {
                        @Override
                        public Boolean getOutput(Integer input) {
                            return input == 15;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "high").build();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_VALUE =
                    AspectReadBuilders.Redstone.BUILDER_INTEGER.handle(AspectReadBuilders.PROP_GET_INTEGER, "value").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_COMPARATOR =
                    AspectReadBuilders.Redstone.BUILDER_INTEGER_COMPARATOR.handle(AspectReadBuilders.PROP_GET_INTEGER, "comparator").build();

        }

        public static final class Inventory {
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_FULL =
                    AspectReadBuilders.Inventory.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<IInventory, Boolean>() {
                        @Override
                        public Boolean getOutput(IInventory inventory) {
                            if(inventory != null) {
                                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                                    ItemStack itemStack = inventory.getStackInSlot(i);
                                    if (itemStack == null) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "full").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EMPTY =
                    AspectReadBuilders.Inventory.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<IInventory, Boolean>() {
                        @Override
                        public Boolean getOutput(IInventory inventory) {
                            if(inventory != null) {
                                for(int i = 0; i < inventory.getSizeInventory(); i++) {
                                    ItemStack itemStack = inventory.getStackInSlot(i);
                                    if(itemStack != null) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "empty").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_NONEMPTY =
                    AspectReadBuilders.Inventory.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<IInventory, Boolean>() {
                        @Override
                        public Boolean getOutput(IInventory inventory) {
                            if(inventory != null) {
                                for(int i = 0; i < inventory.getSizeInventory(); i++) {
                                    ItemStack itemStack = inventory.getStackInSlot(i);
                                    if(itemStack != null) {
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "nonempty").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_APPLICABLE =
                    AspectReadBuilders.Inventory.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<IInventory, Boolean>() {
                        @Override
                        public Boolean getOutput(IInventory inventory) {
                            return inventory != null;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "applicable").build();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_COUNT =
                    AspectReadBuilders.Inventory.BUILDER_INTEGER.handle(new IAspectValuePropagator<IInventory, Integer>() {
                        @Override
                        public Integer getOutput(IInventory inventory) {
                            int count = 0;
                            if(inventory != null) {
                                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                                    ItemStack itemStack = inventory.getStackInSlot(i);
                                    if (itemStack != null) {
                                        count += itemStack.stackSize;
                                    }
                                }
                            }
                            return count;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "count").build();

            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_ITEMSTACKS =
                    AspectReadBuilders.BUILDER_LIST.appendKind("inventory").handle(AspectReadBuilders.Inventory.PROP_GET_LIST, "itemstacks").build();

            public static final IAspectRead<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> OBJECT_ITEM_STACK_SLOT =
                    AspectReadBuilders.Inventory.BUILDER_ITEMSTACK.handle(AspectReadBuilders.PROP_GET_ITEMSTACK).build();

        }

        public static final class World {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_BLOCK =
                    AspectReadBuilders.World.BUILDER_BOOLEAN.handle(new IAspectValuePropagator<DimPos, Boolean>() {
                        @Override
                        public Boolean getOutput(DimPos dimPos) {
                            Block block = dimPos.getWorld().getBlockState(dimPos.getBlockPos()).getBlock();
                            return block != Blocks.air;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "block").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_WEATHER_CLEAR =
                    AspectReadBuilders.World.BUILDER_BOOLEAN.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Boolean>() {
                        @Override
                        public Boolean getOutput(net.minecraft.world.World world) {
                            return !world.isRaining();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "weather").appendKind("clear").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_WEATHER_RAINING =
                    AspectReadBuilders.World.BUILDER_BOOLEAN.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Boolean>() {
                        @Override
                        public Boolean getOutput(net.minecraft.world.World world) {
                            return world.isRaining();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "weather").appendKind("raining").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_WEATHER_THUNDER =
                    AspectReadBuilders.World.BUILDER_BOOLEAN.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Boolean>() {
                        @Override
                        public Boolean getOutput(net.minecraft.world.World world) {
                            return world.isThundering();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "weather").appendKind("thunder").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISDAY =
                    AspectReadBuilders.World.BUILDER_BOOLEAN.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Boolean>() {
                        @Override
                        public Boolean getOutput(net.minecraft.world.World world) {
                            return MinecraftHelpers.isDay(world);
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isday").build();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISNIGHT =
                    AspectReadBuilders.World.BUILDER_BOOLEAN.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Boolean>() {
                        @Override
                        public Boolean getOutput(net.minecraft.world.World world) {
                            return !MinecraftHelpers.isDay(world);
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isnight").build();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_RAINCOUNTDOWN =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Integer>() {
                        @Override
                        public Integer getOutput(net.minecraft.world.World world) {
                            return world.getWorldInfo().getRainTime();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "raincountdown").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_TICKTIME =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Integer>() {
                        @Override
                        public Integer getOutput(net.minecraft.world.World world) {
                            return (int) DoubleMath.mean(MinecraftServer.getServer().worldTickTimes.get(world.provider.getDimensionId()));
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "ticktime").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_DAYTIME =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Integer>() {
                        @Override
                        public Integer getOutput(net.minecraft.world.World world) {
                            return (int) world.getWorldTime() % MinecraftHelpers.MINECRAFT_DAY;
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "daytime").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_LIGHTLEVEL =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(new IAspectValuePropagator<DimPos, Integer>() {
                        @Override
                        public Integer getOutput(DimPos dimPos) {
                            return dimPos.getWorld().getLight(dimPos.getBlockPos());
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "lightlevel").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_PLAYERCOUNT =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Integer>() {
                        @Override
                        public Integer getOutput(net.minecraft.world.World world) {
                            return world.playerEntities.size();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "playercount").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_DIMENSION =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Integer>() {
                        @Override
                        public Integer getOutput(net.minecraft.world.World world) {
                            return world.provider.getDimensionId();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "dimension").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_POSX =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_POS).handle(new IAspectValuePropagator<BlockPos, Integer>() {
                        @Override
                        public Integer getOutput(BlockPos pos) {
                            return pos.getX();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "posx").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_POSY =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_POS).handle(new IAspectValuePropagator<BlockPos, Integer>() {
                        @Override
                        public Integer getOutput(BlockPos pos) {
                            return pos.getY();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "posy").build();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_POSZ =
                    AspectReadBuilders.World.BUILDER_INTEGER.handle(AspectReadBuilders.World.PROP_GET_POS).handle(new IAspectValuePropagator<BlockPos, Integer>() {
                        @Override
                        public Integer getOutput(BlockPos pos) {
                            return pos.getZ();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_INTEGER, "posz").build();

            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_TIME =
                    AspectReadBuilders.World.BUILDER_LONG.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Long>() {
                        @Override
                        public Long getOutput(net.minecraft.world.World world) {
                            return world.getWorldTime();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_LONG, "time").build();
            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_TOTALTIME =
                    AspectReadBuilders.World.BUILDER_LONG.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, Long>() {
                        @Override
                        public Long getOutput(net.minecraft.world.World world) {
                            return world.getTotalWorldTime();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_LONG, "totaltime").build();

            public static final IAspectRead<ValueTypeString.ValueString, ValueTypeString> STRING_NAME =
                    AspectReadBuilders.World.BUILDER_STRING.handle(AspectReadBuilders.World.PROP_GET_WORLD).handle(new IAspectValuePropagator<net.minecraft.world.World, String>() {
                        @Override
                        public String getOutput(net.minecraft.world.World world) {
                            return world.getWorldInfo().getWorldName();
                        }
                    }).handle(AspectReadBuilders.PROP_GET_STRING, "worldname").build();

            public static final IAspectRead<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock> BLOCK =
                    AspectReadBuilders.World.BUILDER_BLOCK.handle(new IAspectValuePropagator<DimPos, IBlockState>() {
                        @Override
                        public IBlockState getOutput(DimPos dimPos) {
                            return dimPos.getWorld().getBlockState(dimPos.getBlockPos());
                        }
                    }).handle(AspectReadBuilders.PROP_GET_BLOCK).build();

            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_ENTITIES =
                    AspectReadBuilders.World.BUILDER_LIST.handle(new IAspectValuePropagator<DimPos, ValueTypeList.ValueList>() {
                        @Override
                        public ValueTypeList.ValueList getOutput(DimPos dimPos) {
                            List<Entity> entities = dimPos.getWorld().getEntitiesInAABBexcluding(null,
                                    new AxisAlignedBB(dimPos.getBlockPos(), dimPos.getBlockPos().add(1, 1, 1)), EntitySelectors.selectAnything);
                            return ValueTypeList.ValueList.ofList(ValueTypes.OBJECT_ENTITY, Lists.transform(entities, new Function<Entity, ValueObjectTypeEntity.ValueEntity>() {
                                @Nullable
                                @Override
                                public ValueObjectTypeEntity.ValueEntity apply(Entity input) {
                                    return ValueObjectTypeEntity.ValueEntity.of(input);
                                }
                            }));
                        }
                    }).appendKind("entities").build();

        }

    }

    // --------------- Read ---------------
    // TODO: remain all to inner static classes

    // --- Fluid ---
    public static final AspectReadBooleanFluidFull READ_BOOLEAN_FLUID_FULL = new AspectReadBooleanFluidFull();
    public static final AspectReadBooleanFluidEmpty READ_BOOLEAN_FLUID_EMPTY = new AspectReadBooleanFluidEmpty();
    public static final AspectReadBooleanFluidNonEmpty READ_BOOLEAN_FLUID_NONEMPTY = new AspectReadBooleanFluidNonEmpty();
    public static final AspectReadBooleanFluidApplicable READ_BOOLEAN_FLUID_APPLICABLE = new AspectReadBooleanFluidApplicable();
    public static final AspectReadBooleanFluidGaseous READ_BOOLEAN_FLUID_GASEOUS = new AspectReadBooleanFluidGaseous();

    public static final AspectReadIntegerFluidAmount READ_INTEGER_FLUID_AMOUNT = new AspectReadIntegerFluidAmount();
    public static final AspectReadIntegerFluidAmountTotal READ_INTEGER_FLUID_AMOUNTTOTAL = new AspectReadIntegerFluidAmountTotal();
    public static final AspectReadIntegerFluidCapacity READ_INTEGER_FLUID_CAPACITY = new AspectReadIntegerFluidCapacity();
    public static final AspectReadIntegerFluidCapacityTotal READ_INTEGER_FLUID_CAPACITYTOTAL = new AspectReadIntegerFluidCapacityTotal();
    public static final AspectReadIntegerFluidTanks READ_INTEGER_FLUID_TANKS = new AspectReadIntegerFluidTanks();
    public static final AspectReadIntegerFluidDensity READ_INTEGER_FLUID_DENSITY = new AspectReadIntegerFluidDensity();
    public static final AspectReadIntegerFluidLuminosity READ_INTEGER_FLUID_LUMINOSITY = new AspectReadIntegerFluidLuminosity();
    public static final AspectReadIntegerFluidTemperature READ_INTEGER_FLUID_TEMPERATURE = new AspectReadIntegerFluidTemperature();
    public static final AspectReadIntegerFluidViscosity READ_INTEGER_FLUID_VISCOSITY = new AspectReadIntegerFluidViscosity();

    public static final AspectReadDoubleFluidFillRatio READ_DOUBLE_FLUID_FILLRATIO = new AspectReadDoubleFluidFillRatio();

    public static final AspectReadStringFluidName READ_STRING_FLUID_NAME = new AspectReadStringFluidName();
    public static final AspectReadStringFluidRarity READ_STRING_FLUID_RARITY = new AspectReadStringFluidRarity();

    public static final AspectReadObjectBlockFluid READ_BLOCK_FLUID_BLOCK = new AspectReadObjectBlockFluid();

    // --- Minecraft ---
    public static final AspectReadIntegerMinecraftRandom READ_INTEGER_MINECRAFT_RANDOM = new AspectReadIntegerMinecraftRandom();
    public static final AspectReadIntegerMinecraftPlayerCount READ_INTEGER_MINECRAFT_PLAYERCOUNT = new AspectReadIntegerMinecraftPlayerCount();
    public static final AspectReadIntegerMinecraftTicktime READ_INTEGER_MINECRAFT_TICKTIME = new AspectReadIntegerMinecraftTicktime();

    // --- Network ---
    public static final AspectReadBooleanNetworkApplicable READ_BOOLEAN_NETWORK_APPLICABLE = new AspectReadBooleanNetworkApplicable();

    public static final AspectReadIntegerNetworkElementCount READ_INTEGER_NETWORK_ELEMENT_COUNT = new AspectReadIntegerNetworkElementCount();
    public static final AspectReadIntegerNetworkEnergyBatteryCount READ_INTEGER_NETWORK_ENERGY_BATTERY_COUNT = new AspectReadIntegerNetworkEnergyBatteryCount();
    public static final AspectReadIntegerNetworkEnergyStored READ_INTEGER_NETWORK_ENERGY_STORED = new AspectReadIntegerNetworkEnergyStored();
    public static final AspectReadIntegerNetworkEnergyMax READ_INTEGER_NETWORK_ENERGY_MAX = new AspectReadIntegerNetworkEnergyMax();

    // --------------- Write ---------------
    public static final AspectWriteBooleanRedstone WRITE_BOOLEAN_REDSTONE = new AspectWriteBooleanRedstone();

    public static final AspectWriteIntegerRedstone WRITE_INTEGER_REDSTONE = new AspectWriteIntegerRedstone();

}
