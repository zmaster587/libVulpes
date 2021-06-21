package zmaster587.libVulpes.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.util.IFluidHandlerInternal;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class TileMultiblockMachine extends TileMultiPowerConsumer {

	public enum NetworkPackets {
		TOGGLE,
		POWERERROR
	}

	private List<ItemStack> inputItemStacks;
	private List<ItemStack> outputItemStacks;
	private List<FluidStack> outputFluidStacks;

	private boolean smartInventoryUpgrade = true;
	//When using smart inventories sometimes setInventory content calls need to be made
	//This flag prevents infinite recursion by having a value of true if any invCheck has started
	private boolean invCheckFlag = false;

	public TileMultiblockMachine() {
		super();
		outputItemStacks = null;
	}

	public List<ItemStack> getInputs() {
		return inputItemStacks;
	}

	public List<ItemStack> getOutputs() {
		return outputItemStacks;
	}
	
	public void setOutputs(List<ItemStack> stack) {
		outputItemStacks = stack;
	}
	
	public void setOutputFluids(List<FluidStack> stack) {
		outputFluidStacks = stack;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		writeToNBT(nbt);
		nbt.setBoolean("built", canRender);
		nbt.setBoolean("hadPowerLastTick", hadPowerLastTick);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}
	
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
		nbt.setBoolean("built", canRender);
		nbt.setBoolean("hadPowerLastTick", hadPowerLastTick);
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		super.handleUpdateTag(nbt);
		canRender = nbt.getBoolean("built");
		hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		canRender = nbt.getBoolean("built");
		hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
		readFromNBT(nbt);
	}

	public void registerRecipes() {
		
	}
	
	@Override
	public void update() {
		//super.update();

		//Freaky janky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0) {
			if(!world.isRemote) {
				if(isComplete())
					canRender = completeStructure = completeStructure(world.getBlockState(pos));
			}
			else {
				SoundEvent str;
				if((str = getSound()) != null) {
					playMachineSound(str);
				}
			}

			timeAlive = 0x1;
		}

		//In case the machine jams for some reason
		if(!isRunning() && world.getTotalWorldTime() % 1000L == 0)
			onInventoryUpdated();

		if(isRunning()) {
			if( hasEnergy(powerPerTick) || (world.isRemote && hadPowerLastTick)) {

				//Increment for both client and server
				onRunningPoweredTick();
				//If server then check to see if we need to update the client, use power and process output if applicable
				if(!world.isRemote) {

					if(!hadPowerLastTick) {
						hadPowerLastTick = true;
						markDirty();
						world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
					}

					useEnergy(powerPerTick);
				}
			}
			else if(!world.isRemote && hadPowerLastTick) { //If server and out of power check to see if client needs update
				hadPowerLastTick = false;
				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
			}
		}
	}

	@Override
	public void setMachineEnabled(boolean enabled) {
		super.setMachineEnabled(enabled);
		onInventoryUpdated();
	}

	@Override
	public void resetCache() {
		super.resetCache();
		batteries.clear();
	}

	/**
	 * @param world world
	 * @param destroyedPos coords of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken, IBlockState state) {
		outputItemStacks = null;
		outputFluidStacks = null;
		super.deconstructMultiBlock(world, destroyedPos, blockBroken, state);
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;
		if(!world.isRemote)
			dumpOutputToInventory();

		inputItemStacks = null;
		outputItemStacks = null;
		outputFluidStacks = null;

		onInventoryUpdated();

		this.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	//When the output of the recipe is dumped to the inventory
	protected void dumpOutputToInventory() {

		int totalItems = 0;
		for(IInventory outInventory : getItemOutPorts()) {
			for(int i = totalItems; i < outputItemStacks.size(); i++) {
				ItemStack stack = outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i);

				if(stack.isEmpty()) {
					outInventory.setInventorySlotContents(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i, outputItemStacks.get(i));
					outInventory.markDirty();
					totalItems++;
				}
				else if(stack.isItemEqual(outputItemStacks.get(i)) && 
						(stack.getCount() + outputItemStacks.get(i).getCount() <= outInventory.getInventoryStackLimit() && stack.getCount() + outputItemStacks.get(i).getCount() <= stack.getMaxStackSize())) {
					outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i).setCount(outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i).getCount() + outputItemStacks.get(i).getCount());
					outInventory.markDirty();
					totalItems++;
				}
			}
		}

		//Handle fluids
		for(int i = 0; i < outputFluidStacks.size() ; i++) {
			fluidOutPorts.get(i).fillInternal(outputFluidStacks.get(i), true);
		}
	}

	//TODO: improve recipe checks
	//Attempt to get a valid recipe given the inputs, null if none found
	protected IRecipe getRecipe(List<IRecipe> set) {

		for(IRecipe recipe : set) {

			if(canProcessRecipe(recipe))
				return recipe;

		}
		return null;
	}

	/**
	 * Provided in case a machine needs to override outputs for some reason without replacing larger functions
	 * @param recipe recipe to get outputs for
	 * @return list of itemstacks the machine can output
	 */
	protected List<ItemStack> getItemOutputs(IRecipe recipe) {
		return recipe.getOutput();
	}

	/**
	 * Provided in case a machine needs to override outputs for some reason without replacing larger functions
	 * @param recipe recipe to get outputs for
	 * @return list of fluidstacks the machine can output
	 */
	protected List<FluidStack> getFluidOutputs(IRecipe recipe) {
		return recipe.getFluidOutputs();
	}


	public void consumeItems(IRecipe recipe) {
		List<List<ItemStack>> ingredients = recipe.getIngredients();

		for (List<ItemStack> ingredient : ingredients) {

			ingredientCheck:

			for (IInventory hatch : getItemInPorts()) {
				for (int i = 0; i < hatch.getSizeInventory(); i++) {
					ItemStack stackInSlot = hatch.getStackInSlot(i);

					for (ItemStack stack : ingredient) {
						if (!stackInSlot.isEmpty() && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.isItemEqual(stack) || (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE && stackInSlot.getItem() == stack.getItem()))) {
							hatch.decrStackSize(i, stack.getCount());
							hatch.markDirty();
							world.notifyBlockUpdate(pos, world.getBlockState(((TileEntity) hatch).getPos()), world.getBlockState(((TileEntity) hatch).getPos()), 6);
							break ingredientCheck;
						}
					}
				}
			}
		}


		//Consume fluids
		int[] fluidInputCounter = new int[recipe.getFluidIngredients().size()];

		for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
			fluidInputCounter[i] = recipe.getFluidIngredients().get(i).amount;
		}

		//Drain Fluid containers
		for(IFluidHandlerInternal fluidInput : fluidInPorts) {
			for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
				FluidStack fluidStack = recipe.getFluidIngredients().get(i).copy();
				fluidStack.amount = fluidInputCounter[i];

				FluidStack drainedFluid;
				drainedFluid = fluidInput.drainInternal(recipe.getFluidIngredients().get(i), true);

				if(drainedFluid != null)
					fluidInputCounter[i] -= drainedFluid.amount;

			}
		}
	}

	//Can this recipe be processed
	public boolean canProcessRecipe(IRecipe recipe) {

		if( !isComplete() || invCheckFlag)
			return false;

		invCheckFlag = true;
		int reservedSpotSize = 0;
		if(recipe instanceof RecipesMachine.Recipe)
			reservedSpotSize = ((RecipesMachine.Recipe)recipe).getRequiredEmptyOutputs();
		
		List<ItemStack> outputItems = getItemOutputs(recipe);

		boolean itemCheck = outputItems.size() == 0;


		List<List<ItemStack>> ingredients = recipe.getIngredients();
		short mask = 0x0;
		recipeCheck:

			for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

				List<ItemStack> ingredient = ingredients.get(ingredientNum);
				ingredientCheck:

					for(IInventory hatch : getItemInPorts()) {

						for(int i = 0; i < hatch.getSizeInventory(); i++) {
							ItemStack stackInSlot = hatch.getStackInSlot(i);

							for(ItemStack stack : ingredient) {
								if(stackInSlot != ItemStack.EMPTY && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.isItemEqual(stack) || (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE && stack.getItem() == stackInSlot.getItem()))) {
									mask |= (1 << ingredientNum);
									break ingredientCheck;
								}
							}
						}

						//If no matching item is found for the ingredient
						//break recipeCheck;
					}


			}
		if(mask != (1 << ( ( ingredients.size() ) )) - 1) {
			invCheckFlag = false;
			return false;
		}


		if(reservedSpotSize < 0)
		{
		//Check output Items
		bottomItemCheck:
			for(IInventory outInventory : getItemOutPorts()) {
				//It's possible for the outputItems to be bigger than the inventory
				if(outInventory.getSizeInventory() - outputItems.size() < 0)
					continue;
				
				for(int i = smartInventoryUpgrade ? outInventory.getSizeInventory() - outputItems.size() : 0; (i < (smartInventoryUpgrade ? outInventory.getSizeInventory() : outputItems.size())); i++) {
					ItemStack stack = outInventory.getStackInSlot(i);

					if(smartInventoryUpgrade) {
						ItemStack outputItem = outputItems.get(outInventory.getSizeInventory() - i - 1);


						boolean allIngredFit = true;
						for(int k = 0; k < outputItems.size() && i - k >= 0; k++) {
							ItemStack stack2 = outInventory.getStackInSlot(outInventory.getSizeInventory()-k-1);
							ItemStack outputItem2  = outputItems.get(k);
							allIngredFit = stack2.isEmpty() || (stack2.isItemEqual(outputItem2) && stack2.getCount() + outputItem2.getCount() <= outInventory.getInventoryStackLimit() && stack2.getCount() + outputItem2.getCount() <= stack.getMaxStackSize());
							
							if(!allIngredFit) break;
						}
						
						//stack cannot be null when assigning flag
						if(allIngredFit) {
							
							//Check all the slots
							invCheckFlag = false;
							itemCheck = true;
							break bottomItemCheck;
						}

						if(stack != ItemStack.EMPTY && ZUtils.getFirstFilledSlotIndex(outInventory) >= outputItems.size()) {
							//Range Check
							int outputSize = outputItems.size();
							int j;
							for(j = 0; j < outputSize; j++) {
								if(outInventory.getStackInSlot(j) != ItemStack.EMPTY) {
									invCheckFlag = false;
									itemCheck = false;
									break bottomItemCheck;
								}
							}

							int numExtraMoves = outInventory.getSizeInventory() - ZUtils.getFirstFilledSlotIndex(outInventory) - 1;

							//J will be last slot in index by now
							for(j = outInventory.getSizeInventory() - outputSize; j > 0; j--) {
								int slot = outInventory.getSizeInventory();
								outInventory.setInventorySlotContents(slot - j - outputSize, outInventory.getStackInSlot(slot - j));
								outInventory.setInventorySlotContents(slot - j, ItemStack.EMPTY);
							}

							invCheckFlag = false;
							itemCheck = true;
							break bottomItemCheck;
						}
					}
					else if(stack == ItemStack.EMPTY || stack.isItemEqual(outputItems.get(i)) && 
							(stack.getCount() + outputItems.get(i).getCount() <= outInventory.getInventoryStackLimit() && stack.getCount() + outputItems.get(i).getCount() <= stack.getMaxStackSize())) {
						itemCheck = true;
						break bottomItemCheck;
					}

				}
			}
		}
		else //Chance output
		{
			for(IInventory outInventory : getItemOutPorts()) {
				for(int i = 0; i < outInventory.getSizeInventory(); i++) {
					if(outInventory.getStackInSlot(i).isEmpty())
						reservedSpotSize--;
				}
			}
			
			if(reservedSpotSize <= 0)
				itemCheck = true;
		}

		int[] fluidInputCounter = new int[recipe.getFluidIngredients().size()];

		//Populate Fluid Counters
		for(IFluidHandlerInternal fluidInput : fluidInPorts) {
			for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
				FluidStack fluidStack = fluidInput.drainInternal(recipe.getFluidIngredients().get(i), false);

				if(fluidStack != null)
					fluidInputCounter[i] += fluidStack.amount;
			}
		}

		invCheckFlag = false;
		for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
			if(fluidInputCounter[i] < recipe.getFluidIngredients().get(i).amount)
				return false;
		}

		//Check outputs


		if(fluidOutPorts.size() < recipe.getFluidOutputs().size())
			return false;

		int[] fluidOutputCounter = new int[recipe.getFluidOutputs().size()];

		//Populate the list
		for(int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			fluidOutputCounter[i] = recipe.getFluidOutputs().get(i).amount;
		}

		//Populate Fluid Counters
		for(int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			fluidOutputCounter[i] -= fluidOutPorts.get(i).fillInternal(recipe.getFluidOutputs().get(i), false);
		}

		for (int value : fluidOutputCounter)
			if (value > 0)
				return false;

		return itemCheck;
	}


	//Used To make sure the multiblock is valid
	/*@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();

		completeStructure = completeStructure();
	}*/


	//Must be overridden or an NPE will occur
	public List<IRecipe> getMachineRecipeList() {
		List<IRecipe> list = RecipesMachine.getInstance().getRecipes(this.getClass());
		return (list != null) ? list : new LinkedList<>();
	}

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	@Override
	public void onInventoryUpdated() {
		//If we are already processing something don't bother

		if(!invCheckFlag && outputItemStacks == null && (outputFluidStacks == null || outputFluidStacks.isEmpty())) {
			IRecipe recipe;

			if(enabled && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe)) {
				consumeItems(recipe);
				powerPerTick = (int)Math.ceil((getPowerMultiplierForRecipe(recipe)*recipe.getPower()));
				completionTime = Math.max((int)(getTimeMultiplierForRecipe(recipe)*recipe.getTime()), 1);
				outputItemStacks = recipe.getOutput();
				outputFluidStacks = recipe.getFluidOutputs();

				List<List<ItemStack>> ingredients = recipe.getIngredients();
				for (List<ItemStack> ingredient : ingredients) {
					if (inputItemStacks == null)
						inputItemStacks = new LinkedList<>(ingredient);
					else
						inputItemStacks.addAll(ingredient);
				}




				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

				setMachineRunning(true); //turn on machine

			}
			else {
				setMachineRunning(false);
			}
		}
	}

	protected float getTimeMultiplierForRecipe(IRecipe recipe) {
		return getTimeMultiplier();
	}

	protected float getPowerMultiplierForRecipe(IRecipe recipe) {
		return 1f;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		//Save output items if applicable
		if(outputItemStacks != null) {
			NBTTagList list = new NBTTagList();
			for(ItemStack stack : outputItemStacks) {
				if(!stack.isEmpty()) {
					NBTTagCompound tag = new NBTTagCompound();
					stack.writeToNBT(tag);
					list.appendTag(tag);
				}
			}
			nbt.setTag("outputItems", list);
		}

		//Save input items if applicable
		if(inputItemStacks != null) {
			NBTTagList list = new NBTTagList();
			for(ItemStack stack : inputItemStacks) {
				if(!stack.isEmpty()) {
					NBTTagCompound tag = new NBTTagCompound();
					stack.writeToNBT(tag);
					list.appendTag(tag);
				}
			}
			nbt.setTag("inputItems", list);
		}

		if(outputFluidStacks != null) {
			NBTTagList list = new NBTTagList();
			for(FluidStack fStack : outputFluidStacks) {
				if(fStack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					fStack.writeToNBT(tag);
					list.appendTag(tag);
				}
			}
			nbt.setTag("outputFluids", list);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		//Load output items being processed if applicable
		if(nbt.hasKey("outputItems")) {
			outputItemStacks = new LinkedList<>();
			NBTTagList list = nbt.getTagList("outputItems", 10);

			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);

				outputItemStacks.add(new ItemStack(tag));
			}
		}

		//Load input items being processed if applicable
		if(nbt.hasKey("inputItems")) {
			inputItemStacks = new LinkedList<>();
			NBTTagList list = nbt.getTagList("inputItems", 10);

			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);

				inputItemStacks.add(new ItemStack(tag));
			}
		}

		//Load output fluids being processed if applicable
		if(nbt.hasKey("outputFluids")) {
			outputFluidStacks = new LinkedList<>();
			NBTTagList list = nbt.getTagList("outputFluids", 10);

			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				outputFluidStacks.add(FluidStack.loadFluidStackFromNBT(tag));
			}
		}
	}

	public boolean attemptCompleteStructure() {
		boolean completeStructure = super.attemptCompleteStructure(world.getBlockState(pos));
		if(completeStructure)
			onInventoryUpdated();

		return completeStructure;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);
	}
}
