package com.ocronite.pillapilla;

import com.badlogic.gdx.math.MathUtils;

class NameGenerator {

	private final static String[] nMasculinos = new String[] {
		"Pie", "Melon", "Moco",
		"Ojo", "Nabo", "Rabo",
		"Ano", "Bigote", "Pozo",
		"Pepino", "Bratwurst", "Duodeno",
		"Tonto", "Anal", "Nugget",
		"Falafel", "Comunista", "Nazi",
		"Prepuber", "Selfie", "Supervisor",
		"Mediocuerpo", "Candelabro", "Pulpo",
		"Negro", "Estupefaciente", "Pulpo",
		"Hermafrodita", "Ventilador", "Vagabundo",
		"Cojon", "Merino", "Smartphone",
		"Hippie", "Hipster", "Amante",
		"Cangrejo", "Pezon", "Orejudo",
		"Cani", "Calvo", "Metaler"
	};

	private final static String[] nFemeninos = new String[] {
		"Cabeza", "Mano", "Vesicula",
		"Semana", "Cobra", "Felacion",
		"Virgensita", "Cara", "Mierda",
		"Prostata", "Flagelacion", "Petarda",
		"Puta", "Col", "Mandragora",
		"Avestruz", "Musica", "Venganza",
		"Feminazi", "Muerte", "Cabra",
		"Choni", "Novia"
	};

	private final static String[] adjVariables = new String[] {
		"Rar", "Cojonud", "Empalmad",
		"Mohos", "Podrid", "Poderos",
		"Turbulent", "Divertid", "Pelud",
		"Oloros", "Fecundad", "Trompeter",
		"Patizamb", "Coj", "Diminut",
		"Gord", "Mortifer", "Bailong",
		"Chuster", "Encocad", "Flacid",
		"Alcoholizad", "Endogamic", "Masturbatori",
		"Motorizad", "Ochenter", "Zoofilic",
		"Barbihuevud", "Escayolad", "Erosionad", 
		"Penetrud", 
	};

	private final static String[] adjInvariables = new String[] {
		"Gigante", "Feliz", "ConBigote",
		"Fecal", "Pozo", "saurio",
		"ConBolsillo", "DeLaMuerte", "ConGarfio",
		"Comunista", "Nazi", "Enclenque",
		"Prepuber", "DeLaJungla", "DeLaMina",
		"Anal", "Azotable", "Penetrante",
		"Mariquita", "DeLaGuadalupe", "Dulce",
		"ConBacon", "Hentai", "DeGominola",
		"ConRetraso", "Especial", "Horrible",
		"ParaBeber", "MadeInChina", "EnBicicleta",
		"PorElCulo", "DeGoma", "DelPorno",
		"EnGallumbos", "Fluorescente", "DeBolsillo",
		"Desechable", "Fucker", "Inminente",
		"DeRegalo", "ParaTodos", "Adolescente",
		"ConPluma", "Eyectable", "Precoz",
		"DeLaSangre"
	};

	private static String NameFromList(String[] list) {
		return list[MathUtils.random(list.length-1)];
	}

	static String generate() {
		String name = "";
		boolean masculino = MathUtils.randomBoolean();

		if(masculino) name += NameFromList(nMasculinos);
		else name += NameFromList(nFemeninos);

		if(MathUtils.randomBoolean(((float)adjVariables.length)/(adjVariables.length+adjInvariables.length))) {
			name += NameFromList(adjVariables);

			if(masculino) name += "o";
			else name += "a";

		} else {
			name += NameFromList(adjInvariables);
		}

		return name;
	}
}
