package test;

import main.domeinLaag.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

public class VluchtTest {

	static LuchtvaartMaatschappij lvm ;
	static Fabrikant f1; 
	static VliegtuigType vtt1; 
	static Vliegtuig vt1;
	static Luchthaven lh1, lh2;
	static Vlucht vl1, vl2; 

	@BeforeEach
	public void initialize() {
		try {
			lvm = new LuchtvaartMaatschappij("NLM");
			f1 = new Fabrikant("Airbus","G. Dejenelle");
			vtt1 = f1.creeervliegtuigtype("A-200", 140);
			Calendar datum = Calendar.getInstance();
			datum.set(2000, 01, 01);
			vt1 = new Vliegtuig(lvm, vtt1, "Luchtbus 100", datum);
			Land l1 = new Land("Nederland", 31);
			Land l2 = new Land("België", 32);
			lh1 = new Luchthaven("Schiphol", "ASD", true, l1);
			lh2 = new Luchthaven("Tegel", "TEG", true, l2);
			Calendar vertr = Calendar.getInstance();
			vertr.set(2020, 03, 30, 14, 15, 0);
			Calendar aank = Calendar.getInstance();
			aank.set(2020, 03, 30, 15, 15, 0);
			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank );
			vertr.set(2020, 4, 1, 8, 15, 0);
			aank.set(2020, 4, 1, 9, 15, 0);
			vl2 = new Vlucht(vt1, lh1, lh2, vertr, aank );
		} catch (Exception e){
			String errorMessage =  "Exception: " + e.getMessage();
			System.out.println(errorMessage); 
		}
	}

	/**
	 * Business rule:
	 * De bestemming moet verschillen van het vertrekpunt van de vlucht.
	 */
	
	@Test
	public void testBestemmingMagNietGelijkZijnAanVertrek_False() {
		Vlucht vlucht = new Vlucht();
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh1);
			Luchthaven bestemming = vlucht.getBestemming();
			assertTrue(bestemming == null);
			vlucht.zetBestemming(lh1);
			// De test zou niet verder mogen komen: er moet al een exception gethrowd zijn.
			bestemming = vlucht.getBestemming();
			assertFalse(bestemming.equals(lh1));
		}
		catch(IllegalArgumentException e) {
			Luchthaven bestemming = vlucht.getBestemming();
			assertFalse(bestemming.equals(lh1));
		}
	}

	@Test
	public void testBestemmingMagNietGelijkZijnAanVertrek_True() {
		Vlucht vlucht = new Vlucht();
		Luchthaven bestemming;
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh2);
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming == null);
			vlucht.zetBestemming(lh1);
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming.equals(lh1));
		}
		catch(IllegalArgumentException e) {
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming.equals(lh1));
		}
	}

	/**
	 * Business rule:
	 * De aankomsttijd moet na de vertrektijd liggen.
	 */

	@Test
	public void testAankomstTijdMagNietLaterZijnDanVertrekTijd_False() {
		Vlucht vlucht = new Vlucht();
		Calendar vertr = Calendar.getInstance();
		vertr.add(Calendar.MINUTE, +1);
		Calendar aank = Calendar.getInstance();
		try {
			vlucht.zetVertrekTijd(vertr);
			vlucht.zetAankomstTijd(aank);
			// Hier hoort een exception te komen
			Calendar aankomst = vlucht.getAankomstTijd();
			assertTrue(aankomst == null);
		}
		catch(VluchtException e) {
			Calendar aankomst = vlucht.getAankomstTijd();
			assertTrue(aankomst == null);
		}
	}

	@Test
	public void testAankomstTijdMagNietLaterZijnDanVertrekTijd_True() {
		Vlucht vlucht = new Vlucht();
		Vlucht vlucht1 = new Vlucht();

		Calendar vertr = Calendar.getInstance();
		Calendar aank = Calendar.getInstance();
		aank.add(Calendar.MINUTE, +1);
		try {
			vlucht.zetVertrekTijd(vertr);
			vlucht.zetAankomstTijd(aank);
			Calendar aankomst = vlucht.getAankomstTijd();
			assertFalse(aankomst == null);
		}
		catch(VluchtException e) {
			Calendar aankomst = vlucht.getAankomstTijd();
			assertFalse(aankomst == null);
		}
	}


	/**
	 * Business rule:
	 * Een vliegtuig kan maar voor één vlucht tegelijk gebruikt worden.
	 */

	@Test
	public void testVertrekTijdMagNietOverlappen_False_AankomstTijdMagNietOverlappen_True() {
		// Fokke bezetten meet een vlucht van Schiphol - Charles de Gaule
		Land l1 = new Land("Nederland", 31);
		Land l2 = new Land("Frankrijk", 32);
		lh1 = new Luchthaven("Schiphol", "ASD", true, l1);
		lh2 = new Luchthaven("Charles de Gaule", "TEG", true, l2);
		Calendar vertr1 = Calendar.getInstance();
		vertr1.set(2025 , 07, 01, 12, 43, 0);
		Calendar aank1 = Calendar.getInstance();
		aank1.set(2025 , 07, 01, 15, 36, 0);
		Vlucht vlucht = new Vlucht(vt1, lh1, lh2, vertr1, aank1);


		Calendar vertr2 = Calendar.getInstance();
		vertr2.set(2025 , 07, 01, 15, 35, 0);
		Calendar aank2 = Calendar.getInstance();
		aank2.set(2025 , 07, 01, 16, 36, 0);
		try {
			vlucht.zetVertrekTijd(vertr2); // Hier moet een Exception komen
			vlucht.zetAankomstTijd(aank2);
			Calendar vertrekTijd = vlucht.getVertrekTijd();
			assertFalse(vertrekTijd.equals(vertr2));
		}
		catch(VluchtException e) {
			Calendar vertrekTijd = vlucht.getVertrekTijd();
			assertFalse(vertrekTijd.equals(vertr2));
		}
	}

	@Test
	public void testVertrekTijdMagNietOverlappen_True_AankomstTijdMagNietOverlappen_False() {
		// Fokke bezetten meet een vlucht van Schiphol - Charles de Gaule
		Land l1 = new Land("Nederland", 31);
		Land l2 = new Land("Frankrijk", 32);
		lh1 = new Luchthaven("Schiphol", "ASD", true, l1);
		lh2 = new Luchthaven("Charles de Gaule", "TEG", true, l2);
		Calendar vertr1 = Calendar.getInstance();
		vertr1.set(2025 , 07, 01, 12, 43, 0);
		Calendar aank1 = Calendar.getInstance();
		aank1.set(2025 , 07, 01, 15, 36, 0);
		Vlucht vlucht = new Vlucht(vt1, lh1, lh2, vertr1, aank1);


		Calendar vertr2 = Calendar.getInstance();
		vertr2.set(2025 , 07, 01, 11, 36, 0);
		Calendar aank2 = Calendar.getInstance();
		aank2.set(2025 , 07, 01, 12, 44, 0);

		try {
			vlucht.zetVertrekTijd(vertr2);
			vlucht.zetAankomstTijd(aank2); // Hier zou een exception moeten komen, maar is fout geimplementeerd
			Calendar aankomstTijd = vlucht.getAankomstTijd();
			assertFalse(aankomstTijd.equals(aank2));
		}
		catch(VluchtException e) {
			Calendar aankomstTijd = vlucht.getAankomstTijd();
			assertFalse(aankomstTijd.equals(aank2));
		}
	}

	@Test
	public void testVertrekTijdMagNietOverlappen_False_AankomstTijdMagNietOverlappen_False() {
		// Fokke bezetten meet een vlucht van Schiphol - Charles de Gaule
		Land l1 = new Land("Nederland", 31);
		Land l2 = new Land("Frankrijk", 32);
		lh1 = new Luchthaven("Schiphol", "ASD", true, l1);
		lh2 = new Luchthaven("Charles de Gaule", "TEG", true, l2);
		Calendar vertr1 = Calendar.getInstance();
		vertr1.set(2025 , 07, 01, 12, 43, 0);
		Calendar aank1 = Calendar.getInstance();
		aank1.set(2025 , 07, 01, 15, 36, 0);
		Vlucht vlucht = new Vlucht(vt1, lh1, lh2, vertr1, aank1);


		Calendar vertr2 = Calendar.getInstance();
		vertr2.set(2025 , 07, 01, 12, 42, 0);
		Calendar aank2 = Calendar.getInstance();
		aank2.set(2025 , 07, 01, 15, 37, 0);
		try {
			vlucht.zetVertrekTijd(vertr2); // Hier zou een exception moeten komen, maar is fout geimplementeerd
			vlucht.zetAankomstTijd(aank2);
			Calendar vertrekTijd = vlucht.getVertrekTijd();
			assertFalse(vertrekTijd.equals(vertr2));
		}
		catch(VluchtException e) {
			Calendar vertrekTijd = vlucht.getVertrekTijd();
			assertFalse(vertrekTijd.equals(vertr2));
		}
	}

	@Test
	public void testVertrekTijdMagNietOverlappen_True_AankomstTijdMagNietOverlappen_True() {
		// Fokke bezetten meet een vlucht van Schiphol - Charles de Gaule
		Land l1 = new Land("Nederland", 31);
		Land l2 = new Land("Frankrijk", 32);
		lh1 = new Luchthaven("Schiphol", "ASD", true, l1);
		lh2 = new Luchthaven("Charles de Gaule", "TEG", true, l2);
		Calendar vertr1 = Calendar.getInstance();
		vertr1.set(2025 , 07, 01, 12, 43, 0);
		Calendar aank1 = Calendar.getInstance();
		aank1.set(2025 , 07, 01, 15, 36, 0);
		Vlucht vlucht = new Vlucht(vt1, lh1, lh2, vertr1, aank1);


		Calendar vertr2 = Calendar.getInstance();
		vertr2.set(2025 , 07, 01, 15, 37, 0);
		Calendar aank2 = Calendar.getInstance();
		aank2.set(2025 , 07, 01, 16, 37, 0);
		try {
			vlucht.zetVertrekTijd(vertr2);
			vlucht.zetAankomstTijd(aank2);
			Calendar vertrekTijd = vlucht.getVertrekTijd();
			assertTrue(vertrekTijd.equals(vertr2));
		}
		catch(VluchtException e) {
			Calendar vertrekTijd = vlucht.getVertrekTijd();
			assertTrue(vertrekTijd.equals(vertr2));
		}
	}

}
