package zmaster587.libVulpes.api.material;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockOre;
import zmaster587.libVulpes.items.ItemOre;
import zmaster587.libVulpes.items.ItemOreProduct;
import zmaster587.libVulpes.util.ItemStackMapping;
import zmaster587.libVulpes.util.OreProductColorizer;

import javax.annotation.Nonnull;
import java.util.*;

public class MaterialRegistry {

	static HashMap<Object, MixedMaterial> mixedMaterialList = new HashMap<>();
	static HashMap<AllowedProducts, List<Block>> productBlockListMapping;
	static List<MaterialRegistry> registries = new LinkedList<>();

	@SideOnly(Side.CLIENT)
	static Object oreProductColorizer;

	public HashMap<String, zmaster587.libVulpes.api.material.Material> strToMaterial = new HashMap<>();
	public List<zmaster587.libVulpes.api.material.Material> materialList = new LinkedList<>();
	public Item[] oreProducts;


	public MaterialRegistry() {
		productBlockListMapping = new HashMap<>();
		registries.add(this);
	}

	public void registerMaterial(zmaster587.libVulpes.api.material.Material material) {
		strToMaterial.put(material.getUnlocalizedName(), material);
		material.setIndex(materialList.size());
		material.registry = this;
		materialList.add(material);
	}

	@SideOnly(Side.CLIENT)
	public void init() {
		if(oreProductColorizer == null)
			oreProductColorizer = new OreProductColorizer();
		//register color handles
		for(Block block : getBlockListForProduct(AllowedProducts.getProductByName("BLOCK"))) {
			if(block == null ||  Item.getItemFromBlock(block) == null)
				continue;
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)oreProductColorizer, block);
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor)oreProductColorizer,  Item.getItemFromBlock(block));
		}


		for(Block block : getBlockListForProduct(AllowedProducts.getProductByName("COIL"))) {
			if(block == null ||  Item.getItemFromBlock(block) == null)
				continue;
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)oreProductColorizer, block);
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor)oreProductColorizer, Item.getItemFromBlock(block));
		}

		for(int i = 0; i < AllowedProducts.getAllAllowedProducts().size(); i++) {
			if(!AllowedProducts.getAllAllowedProducts().get(i).isBlock()) {
				Minecraft.getMinecraft().getItemColors().registerItemColorHandler((OreProductColorizer)oreProductColorizer, oreProducts[i]);
			}
		}

		//getBlockListForProduct(AllowedProducts.getProductByName("ORE"))
	}

	//TODO: allow more block types
	public void registerOres(CreativeTabs tab) {
		int len = materialList.size();
		int numberOfOreBlocks = (len/16) + 1;
		BlockOre ores;
		BlockOre metalBlocks;
		BlockOre coilBlocks;

		List<AllowedProducts> allAllowedProducts = AllowedProducts.getAllAllowedProducts();

		oreProducts = new ItemOreProduct[allAllowedProducts.size()];

		for(int i = 0; i < allAllowedProducts.size(); i++) {

			if(!allAllowedProducts.get(i).isBlock()) {
				oreProducts[i] = new ItemOreProduct(allAllowedProducts.get(i).name().toLowerCase(Locale.ENGLISH)).setCreativeTab(tab);
				LibVulpesBlocks.registerItem(oreProducts[i].setRegistryName("product" + allAllowedProducts.get(i).getName().toLowerCase(Locale.ENGLISH)));
				//GameRegistry.registerItem(oreProducts[i], "product" + allAllowedProducts.get(i).getName().toLowerCase(Locale.ENGLISH));
			}
		}

		boolean oreAllowed = false;
		boolean blockAllowed = false;
		boolean coilAllowed = false;

		for(zmaster587.libVulpes.api.material.Material mat : materialList) {
			if(!oreAllowed)
				oreAllowed = AllowedProducts.getProductByName("ORE").isOfType(mat.getAllowedProducts());
			if(!blockAllowed)
				blockAllowed = AllowedProducts.getProductByName("BLOCK").isOfType(mat.getAllowedProducts());
			if(!coilAllowed)
				coilAllowed = AllowedProducts.getProductByName("COIL").isOfType(mat.getAllowedProducts());
		}

		for(int i = 0; i < numberOfOreBlocks; i++) {

			String name = "ore";
			String metalBlockName = "metal";
			String coilName = "coil";

			metalBlocks = new BlockOre(Material.ROCK);
			metalBlocks.setUnlocalizedName(metalBlockName).setCreativeTab(tab).setHardness(4f);
			metalBlocks.numBlocks = (byte)Math.min(len - (16*i), 16);
			metalBlocks.product = AllowedProducts.getProductByName("BLOCK");

			ores = new BlockOre(Material.ROCK);
			ores.setUnlocalizedName(name).setCreativeTab(tab).setHardness(4f);
			ores.numBlocks = (byte)Math.min(len - (16*i), 16);
			ores.product = AllowedProducts.getProductByName("ORE");

			coilBlocks = new BlockOre(Material.ROCK);
			coilBlocks.setUnlocalizedName(coilName).setCreativeTab(tab).setHardness(4f);
			coilBlocks.numBlocks = (byte)Math.min(len - (16*i), 16);
			coilBlocks.product = AllowedProducts.getProductByName("COIL");

			ores.setRegistryName(name + i);
			metalBlocks.setRegistryName(metalBlockName + i);
			coilBlocks.setRegistryName(coilName + i);

			if(oreAllowed)
				LibVulpesBlocks.registerBlock(ores, ItemOre.class, false);
			if(blockAllowed)
				LibVulpesBlocks.registerBlock(metalBlocks, ItemOre.class, false);
			if(coilAllowed)
				LibVulpesBlocks.registerBlock(coilBlocks, ItemOre.class, false);

			for(int j = 0; j < 16 && j < 16*i + (len % 16); j++) {
				int index = i*16 + j;
				zmaster587.libVulpes.api.material.Material ore = materialList.get(index);

				ores.ores[j] = ore;
				ores.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), ores.getStateFromMeta(j));

				metalBlocks.ores[j] = ore;
				metalBlocks.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), ores.getStateFromMeta(j));

				coilBlocks.ores[j] = ore;
				coilBlocks.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), ores.getStateFromMeta(j));

				for(AllowedProducts product : allAllowedProducts) {
					if(!product.isBlock() && product.isOfType(ore.getAllowedProducts()))
						((ItemOreProduct)oreProducts[product.ordinal()]).registerItem(index, ore);
				}

				for(int g = 0; g < ore.getOreDictNames().length; g++) {
					String str = ore.getOreDictNames()[g];
					if(AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts())) {
						String oreName = "ore" + str;
						OreDictionary.registerOre(oreName, new ItemStack(ores, 1 , j));

						if(FMLCommonHandler.instance().getSide().isClient() && g == 0) {
							oreName = Loader.instance().activeModContainer().getModId() + ":" + oreName;
							Item itemBlock = Item.getItemFromBlock(ores);
							ModelLoader.setCustomModelResourceLocation(itemBlock, j, new ModelResourceLocation(oreName, "inventory"));
						}
					}
					if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts())) {
						String oreName = "block" + str;
						OreDictionary.registerOre(oreName, new ItemStack(metalBlocks, 1 , j));


						if(FMLCommonHandler.instance().getSide().isClient() && g == 0) {
							oreName = Loader.instance().activeModContainer().getModId() + ":" + oreName;
							Item itemBlock = Item.getItemFromBlock(metalBlocks);
							ModelLoader.setCustomModelResourceLocation(itemBlock, j, new ModelResourceLocation(oreName, "inventory"));
						}
					}
					if(AllowedProducts.getProductByName("COIL").isOfType(ore.getAllowedProducts())) {
						String oreName = "coil" + str;
						OreDictionary.registerOre(oreName, new ItemStack(coilBlocks, 1 , j));
						OreDictionary.registerOre("blockCoil", new ItemStack(coilBlocks, 1 , j));
						
						if(FMLCommonHandler.instance().getSide().isClient() && g == 0) {
							oreName = Loader.instance().activeModContainer().getModId() + ":" + oreName;
							Item itemBlock = Item.getItemFromBlock(coilBlocks);
							ModelLoader.setCustomModelResourceLocation(itemBlock, j, new ModelResourceLocation(oreName, "inventory"));
						}
					}
				}
			}

			getBlockListForProduct(AllowedProducts.getProductByName("BLOCK")).add(metalBlocks);
			getBlockListForProduct(AllowedProducts.getProductByName("COIL")).add(coilBlocks);
			getBlockListForProduct(AllowedProducts.getProductByName("ORE")).add(ores);
		}
	}

	public List<Block> getBlockListForProduct(AllowedProducts product) {
		return productBlockListMapping.get(product);
	}

	public Block getBlockForProduct(AllowedProducts product, zmaster587.libVulpes.api.material.Material material, int index) {
		for(Block block : productBlockListMapping.get(product) ) {
			if(((BlockOre)block).ores[index] == material)
				return block;
		}
		return null;
	}

	/**
	 * @param stack the item stack to get the material of
	 * @return {@link zmaster587.libVulpes.api.material.Material} of the itemstack if it exists, otherwise null
	 */
	public static zmaster587.libVulpes.api.material.Material getMaterialFromItemStack(@Nonnull ItemStack stack) {
		Item item = stack.getItem();

		//If items is an itemOreProduct it must have been registered

		for(MaterialRegistry  registry : registries) {
			for(Item i : registry.oreProducts) {
				if(item == i) {
					return registry.materialList.get(stack.getItemDamage());
				}
			}

			int[] ids = OreDictionary.getOreIDs(stack);

			//Check all ores against list
			for(zmaster587.libVulpes.api.material.Material ore : registry.materialList) {
				for(String str : ore.getOreDictNames()) {
					for(int i : ids) {
						if(OreDictionary.getOreName(i).contains(str)) 
							return ore;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param material
	 * @param product
	 * @return an ItemStack of size one containing the product with the given material, or ItemStack.EMPTY if one does not exist
	 */
	@Nonnull
	public static ItemStack getItemStackFromMaterialAndType(String material, AllowedProducts product) {
		return getItemStackFromMaterialAndType(material, product,1);
	}

	/**
	 * @param ore
	 * @param product
	 * @param amount stackSize
	 * @return an ItemStack of stackSize amount containing the product with the given material, or ItemStack.EMPTY if one does not exist
	 */
	@Nonnull
	public static ItemStack getItemStackFromMaterialAndType(String ore, AllowedProducts product, int amount) {
		for(MaterialRegistry  registry : registries) {
			zmaster587.libVulpes.api.material.Material ore2 = registry.strToMaterial.get(ore);

			if(ore2 != null && product != null)
				return new ItemStack( registry.oreProducts[product.ordinal()], amount, ore2.index);
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Registers a mixed material or allow to automate recipe registration
	 * @param material new mixed material to create
	 */
	public static void registerMixedMaterial(MixedMaterial material) {
		Object inputObj = material.getInput();
		if(inputObj instanceof ItemStack && !((ItemStack) inputObj).isEmpty())
			mixedMaterialList.put( new ItemStackMapping((ItemStack) inputObj), material);
		else
			mixedMaterialList.put(inputObj, material);
	}

	/**
	 * @param stack
	 * @return {@link MixedMaterial} that makes up the item, null if the item is not registered
	 */
	public MixedMaterial getMixedMaterial(@Nonnull ItemStack stack) {
		return mixedMaterialList.get(new ItemStackMapping(stack));
	}

	/**
	 * @param str
	 * @return mixed material corresponding to the supplied string example: "bronze"
	 */
	public MixedMaterial getMixedMaterial(String str) {
		return mixedMaterialList.get(str);
	}

	public static int getColorFromItemMaterial(@Nonnull ItemStack stack) {

		zmaster587.libVulpes.api.material.Material material = getMaterialFromItemStack(stack);
		if(material == null) {
			return 0x7a7a7a;
			/*Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
			TextureAtlasSprite tas = (TextureAtlasSprite)stack.getIconIndex();

			IntBuffer pixels = BufferUtils.createIntBuffer(Math.max(tas.getIconWidth() * tas.getIconHeight(),1024));
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, tas.getOriginX(), tas.getOriginY(), tas.getIconWidth(), tas.getIconHeight(), GL11.GL_RGBA, GL11.GL_INT, pixels);
			int[] texture = new int[pixels.remaining()];
			pixels.get(texture);
			int number = 0;

			int r = 0,g = 0,b = 0;
			for (int i = 0; i < Math.sqrt(texture.length); i++) {
				for (int j = 0; j < Math.sqrt(texture.length); j++) {
					int pixel = texture[(i + (j * tas.getIconWidth()))];

					if((pixel & 0xFF) == 0xFF) {
						r += (pixel >>> 24) & 0xFF;
						g += (pixel >>> 16) & 0xFF;
						b += (pixel >>> 8) & 0xFF;
						number++;
					}
				}
			}

			return (((r / number)& 0xFF) << 16  ) | (((g / number)& 0xFF) << 8 ) | ((( b / number ) & 0xFF)   );*/

		}
		else
			return material.getColor();
	}

	/**
	 * @return Collection containing all registered mixed materials
	 */
	public static Collection<MixedMaterial> getMixedMaterialList() {
		return mixedMaterialList.values();
	}

	public static zmaster587.libVulpes.api.material.Material getMaterialFromName(
			String string) {
		for(MaterialRegistry registry : registries) {
			zmaster587.libVulpes.api.material.Material  material = registry.strToMaterial.get(string);

			if(material != null)
				return material;
		}
		return null;
	}

	public static List<zmaster587.libVulpes.api.material.Material> getAllMaterials() {
		List<zmaster587.libVulpes.api.material.Material> list = new LinkedList<>();
		for(MaterialRegistry registry : registries) {
			list.addAll(registry.materialList);
		}

		return list;
	}


}
