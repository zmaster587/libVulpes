package zmaster587.libVulpes.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.tile.TileEntityMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import javax.annotation.Nonnull;
import java.util.*;

public class RecipesMachine {
	
    //This is a bit of a hack
    static class DummyRecipe implements net.minecraft.item.crafting.IRecipe
    {
        private static ItemStack result = new ItemStack(Items.DIAMOND, 64);
        private ResourceLocation name;

        @Override
        public net.minecraft.item.crafting.IRecipe setRegistryName(ResourceLocation name) {
            this.name = name;
            return this;
        }
        @Override public ResourceLocation getRegistryName() { return name; }
        @Override public Class<net.minecraft.item.crafting.IRecipe> getRegistryType() { return net.minecraft.item.crafting.IRecipe.class; }
        @Override public boolean matches(InventoryCrafting inv, World worldIn) { return false; } //dirt?
        @Override @Nonnull public ItemStack getCraftingResult(InventoryCrafting inv) { return result; }
        @Override public boolean canFit(int width, int height) { return false; }
        @Override @Nonnull public ItemStack getRecipeOutput() { return result; }
        @Override public boolean isHidden() { return true; }
    }
	
    public static class ChanceItemStack
    {
    	public ItemStack stack;
    	public float chance;
    	
    	public ChanceItemStack(@Nonnull ItemStack stack, float chance)
    	{
    		this.stack = stack;
    		this.chance = chance;
    	}
    }
    
    public static class ChanceFluidStack
    {
    	public FluidStack stack;
    	public float chance;
    	public ChanceFluidStack(FluidStack stack, float chance)
    	{
    		this.stack = stack;
    		this.chance = chance;
    	}

    }
    
	public static class Recipe implements IRecipe {

		private List<List<ItemStack>> input;
		private Map<Integer, String> inputOreDict;
		private LinkedList<FluidStack> fluidInput;
		private LinkedList<ChanceItemStack> output;
		private LinkedList<ChanceFluidStack> fluidOutput;
		private int completionTime, power;
		private int maxOutputSize;

		public Recipe() {}

		public Recipe(List<ChanceItemStack> output, List<List<ItemStack>> input, int completionTime, int powerReq, Map<Integer, String> oreDict) {
			this.output = new LinkedList<>();
			this.output.addAll(output);

			this.input = new LinkedList<>();
		
			this.input.addAll(input);

			this.completionTime = completionTime;
			this.power = powerReq;

			this.fluidInput = new LinkedList<>();
			this.fluidOutput = new LinkedList<>();
			
			this.inputOreDict = oreDict;
			
			maxOutputSize = -1;
		}

		public Recipe(List<ChanceItemStack> output, List<List<ItemStack>> input, List<ChanceFluidStack> fluidOutput, List<FluidStack> fluidInput, int completionTime, int powerReq, Map<Integer, String> oreDict) {
			this(output, input, completionTime, powerReq, oreDict);

			this.fluidInput.addAll(fluidInput);
			this.fluidOutput.addAll(fluidOutput);
		}

		public void setMaxOutputSize(int size) { maxOutputSize = size; }
		
		public int getCompletionTime() {return completionTime; }


		public int getPowerReq() {
			return power;
		}

		@Override
		public List<List<ItemStack>> getIngredients() {
			return input;
		}
		
		public String getOreDictString(int slot) { return inputOreDict == null ? null : inputOreDict.get(slot); }

		@Override
		public List<FluidStack> getFluidIngredients() {
			return fluidInput;
		}
		
		@Override
		public List<FluidStack> getFluidOutputs() {
			List<FluidStack> stacks = new LinkedList<>();
			fluidOutput.forEach((ChanceFluidStack s) -> stacks.add(s.stack));
			return stacks;
		}

		@Override
		public int getTime() {
			return completionTime;
		}

		@Override
		public int getPower() {
			return power;
		}

		@Override
		public List<ItemStack> getOutput() {
			ArrayList<ItemStack> stack = new ArrayList<>();

			int maxOutputSize = getRequiredEmptyOutputs();
			
			if(maxOutputSize > 0)
			{
				float maxChance = 0;
				Random rand = new Random(System.currentTimeMillis());
				for(ChanceItemStack i : output) {
					maxChance += i.chance;
				}
				
				for(int i = 0; i < maxOutputSize; i++)
				{
					float currentHit = rand.nextFloat()*maxChance;
					float currChance = 0;
					ItemStack nextStack = output.get(0).stack;
					for(ChanceItemStack currStack : output) {
						currChance += currStack.chance;
						if(currChance > currentHit)
						{
							stack.add(nextStack.copy());
							break;
						}
						nextStack = currStack.stack;
					}
				}
			}
			else
			{
				for(ChanceItemStack i : output) {
					stack.add(i.stack.copy());
				}
			}

			return stack;
		}
		
		public List<ChanceItemStack> getChanceOutputs()
		{
			return output;
		}

		public int getRequiredEmptyOutputs()
		{
			return maxOutputSize;
		}

		public boolean outputToOnlyEmptySlots()
		{
			return maxOutputSize != -1;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Recipe) {
				Recipe otherRecipe = (Recipe)obj;
				if(input.size() != otherRecipe.input.size() || fluidInput.size() != otherRecipe.fluidInput.size())
					return false;

				for(int i = 0; i < input.size(); i++) {
					if(input.get(i).size() != otherRecipe.input.get(i).size())
						return false;
					for(int j = 0; j < input.get(i).size(); j++) {
						if(!ItemStack.areItemStacksEqual(input.get(i).get(j), otherRecipe.input.get(i).get(j)))
							return false;
					}
				}

				for(int i = 0; i < fluidInput.size(); i++) {
					if(fluidInput.get(i).getFluid() != otherRecipe.fluidInput.get(i).getFluid())
						return false;
				}
			}

			return true;
		}
	}

	public HashMap<Class<? extends TileMultiblockMachine>, List<IRecipe>> recipeList;

	private static RecipesMachine instance = new RecipesMachine();

	public RecipesMachine() {
		recipeList = new HashMap<>();
	}

	public static RecipesMachine getInstance() { return instance; }

	public void clearRecipes(Class<? extends TileEntityMachine> clazz) {
		recipeList.get(clazz).clear();
	}
	
	public void addRecipe(Class clazz , Object[] out, int timeRequired, int power, Object ... inputs) {
		List<IRecipe> recipes = getRecipes(clazz);
		if(recipes == null) {
			recipes = new LinkedList<>();
			recipeList.put(clazz,recipes);
		}

		Map<Integer, String> oreDict = new HashMap<>();
		LinkedList<List<ItemStack>> stack = new LinkedList<>();

		ArrayList<FluidStack> inputFluidStacks = new ArrayList<>();

		try {
			for(int i = 0; i < inputs.length; i++) {
				LinkedList<ItemStack> innerList = new LinkedList<>();
				if(inputs[i] != null) {
					if(inputs[i] instanceof String) {
						oreDict.put(i, ((String)inputs[i]));
						for (ItemStack itemStack : OreDictionary.getOres((String)inputs[i])) {
							innerList.add(itemStack.copy());
						}
					}
					else if(inputs[i] instanceof NumberedOreDictStack) {
						oreDict.put(i, ((NumberedOreDictStack)inputs[i]).ore);
						for (ItemStack itemStack : OreDictionary.getOres(((NumberedOreDictStack)inputs[i]).getOre())) {
							int number  = ((NumberedOreDictStack)inputs[i]).getNumber();
							ItemStack stack2 = itemStack.copy();
							stack2.setCount(number);
							innerList.add(stack2);
						}
					}
					else if(inputs[i] instanceof FluidStack)
						inputFluidStacks.add((FluidStack) inputs[i]);
					else if(inputs[i] instanceof Item)
						inputs[i] = new ItemStack((Item)inputs[i]);
					else if(inputs[i] instanceof Block)
						inputs[i] = new ItemStack((Block)inputs[i]);
					else if(inputs[i] instanceof ItemStack && !((ItemStack) (inputs[i])).isEmpty())
						innerList.add((ItemStack)inputs[i]);
				}
				if(!innerList.isEmpty())
				stack.add(innerList);
			}
			ArrayList<ChanceItemStack> outputItem = new ArrayList<>();
			ArrayList<ChanceFluidStack> outputFluidStacks = new ArrayList<>();

			for(Object outputObject : out) {
				if(outputObject instanceof ItemStack && !((ItemStack) outputObject).isEmpty())
					outputItem.add(new ChanceItemStack((ItemStack)outputObject, 0f));
				else if(outputObject instanceof FluidStack)
					outputFluidStacks.add(new ChanceFluidStack((FluidStack)outputObject, 0f));
			}

			Recipe recipe;
			if(inputFluidStacks.isEmpty() && outputFluidStacks.isEmpty())
				recipe = new Recipe(outputItem, stack, timeRequired, power, oreDict);
			else
				recipe = new Recipe(outputItem, stack, outputFluidStacks, inputFluidStacks, timeRequired, power, oreDict);

			if(recipes.contains(recipe)) 
				LibVulpes.logger.info("Overwriting recipe " + recipes.remove(recipe));
			recipes.add(recipe);

		} catch(ClassCastException e) {
			//Custom handling to make sure it logs and can be suppressed by user
			StringBuilder message = new StringBuilder(e.getLocalizedMessage());

			for(StackTraceElement element : e.getStackTrace()) {
				message.append("\n\t").append(element.toString());
			}

			LibVulpes.logger.warn("Cannot add recipe!");
			LibVulpes.logger.warn(message.toString());

		}
	}

	/**
	 * @param clazz Class object of the machine to register the recipe
	 * @param out outout object of the machine, accepts itemStack and fluidStacks
	 * @param timeRequired base running time for the recipe in ticks
	 * @param power power units per tick
	 * @param inputs input objects for the recipe, accepts forge ore dict entries as strings, itemStacks, Items, Blocks, and fluidStacks
	 */
	public void addRecipe(Class clazz , Object out, int timeRequired, int power, Object ... inputs) {
		addRecipe(clazz, new Object[] {out}, timeRequired, power, inputs);
	}
	
	public void addRecipe(Class clazz , List<Object> out, int timeRequired, int power, List<Object> inputs) {
		
		Object[] outputs = new Object[out.size()];
		outputs = out.toArray(outputs);
		
		Object[] inputs2 = new Object[inputs.size()];
		inputs2 = inputs.toArray(inputs2);
		
		addRecipe(clazz, outputs, timeRequired, power, inputs2);
	}

	//Given the class return the list
	public List<IRecipe> getRecipes(Class clazz) {
		return recipeList.get(clazz);
	}
}