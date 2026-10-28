package nc.handler;

import nc.NuclearCraft;
import net.minecraft.world.World;
import net.sxmaa.u25core.api.radiation.RadiationAPI;
import cpw.mods.fml.common.Loader;

/** Radiation through U25Core. Without it there is none; only {@link #LOADED} is touched then. */
public final class U25CoreRadiation {

	public static final boolean LOADED = Loader.isModLoaded("u25core");

	private U25CoreRadiation() {}

	/** Ingot radioactivity in Sv/h: 1E-6 * (specific activity / U-238's)^0.25, so plutonium lands near U25Core's default. */
	private static final Object[] ISOTOPES = {
		"U238", 1.0E-6, "U235", 1.6E-6, "U233", 1.3E-5, "Th232", 7.6E-7, "Th230", 1.6E-5,
		"Pu238", 8.5E-5, "Pu239", 2.1E-5, "Pu241", 1.3E-4, "Pu242", 1.1E-5, "Np236", 1.4E-5, "Np237", 6.8E-6,
		"Am241", 5.7E-5, "Am242", 7.4E-5, "Am243", 2.8E-5, "Cm243", 1.1E-4, "Cm245", 2.7E-5, "Cm246", 3.1E-5,
		"Cm247", 4.1E-6, "Cf250", 1.35E-4
	};

	/** Fuel name and the isotope it is enriched with. */
	private static final String[] FUELS = {
		"LEU235", "U235", "HEU235", "U235", "LEP239", "Pu239", "HEP239", "Pu239", "MOX239", "Pu239", "TBU", "Th232",
		"LEU233", "U233", "HEU233", "U233", "LEP241", "Pu241", "HEP241", "Pu241", "MOX241", "Pu241",
		"LEN236", "Np236", "HEN236", "Np236", "LEA242", "Am242", "HEA242", "Am242", "LEC243", "Cm243", "HEC243", "Cm243",
		"LEC245", "Cm245", "HEC245", "Cm245", "LEC247", "Cm247", "HEC247", "Cm247"
	};

	public static void registerMaterials() {
		for (int i = 0; i < ISOTOPES.length; i += 2) {
			String name = (String) ISOTOPES[i];
			double value = (Double) ISOTOPES[i + 1];
			for (String form : new String[] {"", "Base", "Oxide"}) {
				RadiationAPI.registerRadioactive("ore:" + name + form, value);
				RadiationAPI.registerRadioactive("ore:tiny" + name + form, value/9);
			}
		}
		for (int i = 0; i < FUELS.length; i += 2) {
			String fuel = FUELS[i];
			double fresh = isotope(FUELS[i + 1])*(fuel.startsWith("HE") ? 4 : 1);
			for (String form : new String[] {"", "Oxide", "Cell", "CellOxide"}) RadiationAPI.registerRadioactive("ore:" + fuel + form, fresh);
			RadiationAPI.registerRadioactive("ore:d" + fuel + "Cell", NuclearCraft.spentFuelRadiation);
			RadiationAPI.registerRadioactive("ore:d" + fuel + "CellOxide", NuclearCraft.spentFuelRadiation);
		}
	}

	private static double isotope(String name) {
		for (int i = 0; i < ISOTOPES.length; i += 2) if (ISOTOPES[i].equals(name)) return (Double) ISOTOPES[i + 1];
		return 0;
	}

	public static void contaminate(World world, double x, double y, double z, double svPerHour) {
		if (LOADED && svPerHour > 0) RadiationAPI.contaminate(world, x, y, z, svPerHour);
	}
}
