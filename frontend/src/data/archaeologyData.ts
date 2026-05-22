export interface Artefact {
  name: string
  wikiUrl: string
}

export interface Collection {
  name: string
  requiredLevel: number
  collector: string
  artefacts: Artefact[]
}

export interface CollectorGroup {
  name: string
  collections: Collection[]
}

export const COLLECTOR_GROUPS: CollectorGroup[] = [
  {
    name: 'Soran, Emissary of Zaros',
    collections: [
      {
        name: 'Zarosian I', requiredLevel: 5, collector: 'Soran',
        artefacts: [
          { name: 'Venator dagger', wikiUrl: 'https://runescape.wiki/w/Venator_dagger' },
          { name: 'Venator light crossbow', wikiUrl: 'https://runescape.wiki/w/Venator_light_crossbow' },
          { name: 'Primis Elementis standard', wikiUrl: 'https://runescape.wiki/w/Primis_Elementis_standard' },
          { name: 'Legionary gladius', wikiUrl: 'https://runescape.wiki/w/Legionary_gladius' },
          { name: 'Legionary square shield', wikiUrl: 'https://runescape.wiki/w/Legionary_square_shield' },
          { name: 'Zaros effigy', wikiUrl: 'https://runescape.wiki/w/Zaros_effigy' },
          { name: 'Zarosian training dummy', wikiUrl: 'https://runescape.wiki/w/Zarosian_training_dummy' },
          { name: 'Legatus Maximus figurine', wikiUrl: 'https://runescape.wiki/w/Legatus_Maximus_figurine' },
          { name: "'Solem in Umbra' painting", wikiUrl: "https://runescape.wiki/w/'Solem_in_Umbra'_painting" },
        ],
      },
      {
        name: 'Zarosian II', requiredLevel: 47, collector: 'Soran',
        artefacts: [
          { name: 'Ancient timepiece', wikiUrl: 'https://runescape.wiki/w/Ancient_timepiece' },
          { name: 'Legatus pendant', wikiUrl: 'https://runescape.wiki/w/Legatus_pendant' },
          { name: 'Pontifex signet ring', wikiUrl: 'https://runescape.wiki/w/Pontifex_signet_ring' },
          { name: "'Incite Fear' spell scroll", wikiUrl: "https://runescape.wiki/w/'Incite_Fear'_spell_scroll" },
          { name: 'Amphitheatre debris', wikiUrl: 'https://runescape.wiki/w/Amphitheatre_debris' },
          { name: 'Castra debris', wikiUrl: 'https://runescape.wiki/w/Castra_debris' },
          { name: 'Praetorium debris', wikiUrl: 'https://runescape.wiki/w/Praetorium_debris' },
          { name: 'Triumphal arch debris', wikiUrl: 'https://runescape.wiki/w/Triumphal_arch_debris' },
          { name: 'War table debris', wikiUrl: 'https://runescape.wiki/w/War_table_debris' },
          { name: 'Shadow weapon rack', wikiUrl: 'https://runescape.wiki/w/Shadow_weapon_rack' },
        ],
      },
      {
        name: 'Zarosian III', requiredLevel: 81, collector: 'Soran',
        artefacts: [
          { name: 'Armadylean short bow', wikiUrl: 'https://runescape.wiki/w/Armadylean_short_bow' },
          { name: 'Cloudburst staff', wikiUrl: 'https://runescape.wiki/w/Cloudburst_staff' },
          { name: 'Howl scraps', wikiUrl: 'https://runescape.wiki/w/Howl_scraps' },
          { name: 'Hurricane spin top', wikiUrl: 'https://runescape.wiki/w/Hurricane_spin_top' },
          { name: 'Omnidirectional dome', wikiUrl: 'https://runescape.wiki/w/Omnidirectional_dome' },
          { name: 'Stormguard telescope', wikiUrl: 'https://runescape.wiki/w/Stormguard_telescope' },
          { name: 'Thunderwing butterfly display', wikiUrl: 'https://runescape.wiki/w/Thunderwing_butterfly_display' },
          { name: 'Aviansie windchime', wikiUrl: 'https://runescape.wiki/w/Aviansie_windchime' },
          { name: 'Flight recorder', wikiUrl: 'https://runescape.wiki/w/Flight_recorder' },
          { name: 'Prototype gravimetric generator', wikiUrl: 'https://runescape.wiki/w/Prototype_gravimetric_generator' },
        ],
      },
      {
        name: 'Zarosian IV', requiredLevel: 100, collector: 'Soran',
        artefacts: [
          { name: 'Bandosian weapon fragment', wikiUrl: 'https://runescape.wiki/w/Bandosian_weapon_fragment' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: 'Warforge pig iron', wikiUrl: 'https://runescape.wiki/w/Warforge_pig_iron' },
          { name: "Yu'biusk clay", wikiUrl: "https://runescape.wiki/w/Yu'biusk_clay" },
          { name: 'Garagorshuun war hammer', wikiUrl: 'https://runescape.wiki/w/Garagorshuun_war_hammer' },
          { name: 'Ourg prayer beads', wikiUrl: 'https://runescape.wiki/w/Ourg_prayer_beads' },
          { name: 'Ourg megahitter', wikiUrl: 'https://runescape.wiki/w/Ourg_megahitter' },
          { name: 'Ourg bone', wikiUrl: 'https://runescape.wiki/w/Ourg_bone' },
          { name: "Dorgeshuun c'bow", wikiUrl: "https://runescape.wiki/w/Dorgeshuun_c'bow_(artefact)" },
        ],
      },
    ],
  },
  {
    name: 'Isaura',
    collections: [
      {
        name: 'Zamorakian I', requiredLevel: 20, collector: 'Isaura',
        artefacts: [
          { name: 'Hookah pipe', wikiUrl: 'https://runescape.wiki/w/Hookah_pipe' },
          { name: 'Opulent wine goblet', wikiUrl: 'https://runescape.wiki/w/Opulent_wine_goblet' },
          { name: 'Crest of Dagon', wikiUrl: 'https://runescape.wiki/w/Crest_of_Dagon' },
          { name: "'Disorder' painting", wikiUrl: "https://runescape.wiki/w/'Disorder'_painting" },
          { name: 'Order of Dis robes', wikiUrl: 'https://runescape.wiki/w/Order_of_Dis_robes' },
          { name: 'Ritual dagger', wikiUrl: 'https://runescape.wiki/w/Ritual_dagger' },
          { name: 'Imp mask', wikiUrl: 'https://runescape.wiki/w/Imp_mask' },
          { name: 'Lesser demon mask', wikiUrl: 'https://runescape.wiki/w/Lesser_demon_mask' },
          { name: 'Greater demon mask', wikiUrl: 'https://runescape.wiki/w/Greater_demon_mask' },
        ],
      },
      {
        name: 'Zamorakian II', requiredLevel: 45, collector: 'Isaura',
        artefacts: [
          { name: 'Branding iron', wikiUrl: 'https://runescape.wiki/w/Branding_iron' },
          { name: 'Manacles', wikiUrl: 'https://runescape.wiki/w/Manacles' },
          { name: 'Dis dungeon key', wikiUrl: 'https://runescape.wiki/w/Dis_dungeon_key' },
          { name: 'Dis dungeon shackles', wikiUrl: 'https://runescape.wiki/w/Dis_dungeon_shackles' },
          { name: 'Infernal art', wikiUrl: 'https://runescape.wiki/w/Infernal_art' },
          { name: 'Soul obelisk shard', wikiUrl: 'https://runescape.wiki/w/Soul_obelisk_shard' },
          { name: 'Soul urn', wikiUrl: 'https://runescape.wiki/w/Soul_urn' },
          { name: 'Zamorakian insignia', wikiUrl: 'https://runescape.wiki/w/Zamorakian_insignia_(artefact)' },
          { name: 'Zamorakian tablet', wikiUrl: 'https://runescape.wiki/w/Zamorakian_tablet' },
        ],
      },
      {
        name: 'Zamorakian III', requiredLevel: 89, collector: 'Isaura',
        artefacts: [
          { name: 'Bandosian weapon fragment', wikiUrl: 'https://runescape.wiki/w/Bandosian_weapon_fragment' },
          { name: 'Garagorshuun war hammer', wikiUrl: 'https://runescape.wiki/w/Garagorshuun_war_hammer' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: 'Warforge pig iron', wikiUrl: 'https://runescape.wiki/w/Warforge_pig_iron' },
          { name: "Yu'biusk clay", wikiUrl: "https://runescape.wiki/w/Yu'biusk_clay" },
          { name: 'Ourg megahitter', wikiUrl: 'https://runescape.wiki/w/Ourg_megahitter' },
          { name: 'Ourg prayer beads', wikiUrl: 'https://runescape.wiki/w/Ourg_prayer_beads' },
          { name: 'Ourg bone', wikiUrl: 'https://runescape.wiki/w/Ourg_bone' },
          { name: "Dorgeshuun c'bow", wikiUrl: "https://runescape.wiki/w/Dorgeshuun_c'bow_(artefact)" },
        ],
      },
      {
        name: 'Zamorakian IV', requiredLevel: 107, collector: 'Isaura',
        artefacts: [
          { name: 'Aviansie dreamcoat', wikiUrl: 'https://runescape.wiki/w/Aviansie_dreamcoat' },
          { name: 'Stormguard telescope', wikiUrl: 'https://runescape.wiki/w/Stormguard_telescope' },
          { name: 'Hurricane spin top', wikiUrl: 'https://runescape.wiki/w/Hurricane_spin_top' },
          { name: 'Omnidirectional dome', wikiUrl: 'https://runescape.wiki/w/Omnidirectional_dome' },
          { name: 'Thunderwing butterfly display', wikiUrl: 'https://runescape.wiki/w/Thunderwing_butterfly_display' },
        ],
      },
    ],
  },
  {
    name: 'Sir Atcha',
    collections: [
      {
        name: 'Saradominist I', requiredLevel: 42, collector: 'Sir Atcha',
        artefacts: [
          { name: "'Frying pan'", wikiUrl: "https://runescape.wiki/w/'Frying_pan'" },
          { name: 'Hallowed lantern', wikiUrl: 'https://runescape.wiki/w/Hallowed_lantern' },
          { name: 'Ceremonial unicorn ornament', wikiUrl: 'https://runescape.wiki/w/Ceremonial_unicorn_ornament' },
          { name: 'Ceremonial unicorn saddle', wikiUrl: 'https://runescape.wiki/w/Ceremonial_unicorn_saddle' },
          { name: 'Everlight harp', wikiUrl: 'https://runescape.wiki/w/Everlight_harp' },
          { name: 'Everlight trumpet', wikiUrl: 'https://runescape.wiki/w/Everlight_trumpet' },
          { name: 'Everlight violin', wikiUrl: 'https://runescape.wiki/w/Everlight_violin' },
          { name: 'Folded-arm figurine (female)', wikiUrl: 'https://runescape.wiki/w/Folded-arm_figurine_(female)' },
          { name: 'Folded-arm figurine (male)', wikiUrl: 'https://runescape.wiki/w/Folded-arm_figurine_(male)' },
        ],
      },
      {
        name: 'Saradominist II', requiredLevel: 61, collector: 'Sir Atcha',
        artefacts: [
          { name: 'Dominion discus', wikiUrl: 'https://runescape.wiki/w/Dominion_discus' },
          { name: 'Dominion javelin', wikiUrl: 'https://runescape.wiki/w/Dominion_javelin' },
          { name: 'Dominion sword', wikiUrl: 'https://runescape.wiki/w/Dominion_sword' },
          { name: 'Funeral urn (Saradominist)', wikiUrl: 'https://runescape.wiki/w/Funeral_urn_(Saradominist)' },
          { name: 'Icyenic armour fragment', wikiUrl: 'https://runescape.wiki/w/Icyenic_armour_fragment' },
          { name: 'Icyenic bow', wikiUrl: 'https://runescape.wiki/w/Icyenic_bow' },
          { name: 'Icyenic defence scroll', wikiUrl: 'https://runescape.wiki/w/Icyenic_defence_scroll' },
          { name: 'Icyenic pendant', wikiUrl: 'https://runescape.wiki/w/Icyenic_pendant' },
          { name: 'Icyenic prayer scroll', wikiUrl: 'https://runescape.wiki/w/Icyenic_prayer_scroll' },
        ],
      },
      {
        name: 'Saradominist III', requiredLevel: 95, collector: 'Sir Atcha',
        artefacts: [
          { name: 'Bandosian weapon fragment', wikiUrl: 'https://runescape.wiki/w/Bandosian_weapon_fragment' },
          { name: 'Garagorshuun war hammer', wikiUrl: 'https://runescape.wiki/w/Garagorshuun_war_hammer' },
          { name: 'Ourg bone', wikiUrl: 'https://runescape.wiki/w/Ourg_bone' },
          { name: 'Ourg megahitter', wikiUrl: 'https://runescape.wiki/w/Ourg_megahitter' },
          { name: 'Ourg prayer beads', wikiUrl: 'https://runescape.wiki/w/Ourg_prayer_beads' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: 'Warforge pig iron', wikiUrl: 'https://runescape.wiki/w/Warforge_pig_iron' },
          { name: "Yu'biusk clay", wikiUrl: "https://runescape.wiki/w/Yu'biusk_clay" },
        ],
      },
      {
        name: 'Saradominist IV', requiredLevel: 109, collector: 'Sir Atcha',
        artefacts: [
          { name: 'Aviansie dreamcoat', wikiUrl: 'https://runescape.wiki/w/Aviansie_dreamcoat' },
          { name: 'Flight recorder', wikiUrl: 'https://runescape.wiki/w/Flight_recorder' },
          { name: 'Hurricane spin top', wikiUrl: 'https://runescape.wiki/w/Hurricane_spin_top' },
          { name: 'Omnidirectional dome', wikiUrl: 'https://runescape.wiki/w/Omnidirectional_dome' },
          { name: 'Prototype gravimetric generator', wikiUrl: 'https://runescape.wiki/w/Prototype_gravimetric_generator' },
        ],
      },
    ],
  },
  {
    name: 'Lowse',
    collections: [
      {
        name: 'Armadylean I', requiredLevel: 69, collector: 'Lowse',
        artefacts: [
          { name: 'Armadylean short bow', wikiUrl: 'https://runescape.wiki/w/Armadylean_short_bow' },
          { name: 'Cloudburst staff', wikiUrl: 'https://runescape.wiki/w/Cloudburst_staff' },
          { name: 'Howl scraps', wikiUrl: 'https://runescape.wiki/w/Howl_scraps' },
          { name: 'Hurricane spin top', wikiUrl: 'https://runescape.wiki/w/Hurricane_spin_top' },
          { name: 'Omnidirectional dome', wikiUrl: 'https://runescape.wiki/w/Omnidirectional_dome' },
          { name: 'Stormguard telescope', wikiUrl: 'https://runescape.wiki/w/Stormguard_telescope' },
          { name: 'Thunderwing butterfly display', wikiUrl: 'https://runescape.wiki/w/Thunderwing_butterfly_display' },
          { name: 'Aviansie windchime', wikiUrl: 'https://runescape.wiki/w/Aviansie_windchime' },
          { name: 'Flight recorder', wikiUrl: 'https://runescape.wiki/w/Flight_recorder' },
        ],
      },
      {
        name: 'Armadylean II', requiredLevel: 81, collector: 'Lowse',
        artefacts: [
          { name: 'Prototype gravimetric generator', wikiUrl: 'https://runescape.wiki/w/Prototype_gravimetric_generator' },
          { name: 'Armadylean short bow', wikiUrl: 'https://runescape.wiki/w/Armadylean_short_bow' },
          { name: 'Armadylean cloak', wikiUrl: 'https://runescape.wiki/w/Armadylean_cloak' },
          { name: 'Armadylean chaps', wikiUrl: 'https://runescape.wiki/w/Armadylean_chaps' },
          { name: 'Armadylean coif', wikiUrl: 'https://runescape.wiki/w/Armadylean_coif' },
          { name: 'Armadylean vambraces', wikiUrl: 'https://runescape.wiki/w/Armadylean_vambraces' },
          { name: 'Armadylean body', wikiUrl: 'https://runescape.wiki/w/Armadylean_body' },
          { name: 'Armadylean warstaff', wikiUrl: 'https://runescape.wiki/w/Armadylean_warstaff' },
        ],
      },
      {
        name: 'Armadylean III', requiredLevel: 108, collector: 'Lowse',
        artefacts: [
          { name: 'Aviansie dreamcoat', wikiUrl: 'https://runescape.wiki/w/Aviansie_dreamcoat' },
          { name: 'Stormguard telescope', wikiUrl: 'https://runescape.wiki/w/Stormguard_telescope' },
          { name: 'Hurricane spin top', wikiUrl: 'https://runescape.wiki/w/Hurricane_spin_top' },
          { name: 'Omnidirectional dome', wikiUrl: 'https://runescape.wiki/w/Omnidirectional_dome' },
          { name: 'Thunderwing butterfly display', wikiUrl: 'https://runescape.wiki/w/Thunderwing_butterfly_display' },
        ],
      },
    ],
  },
  {
    name: 'Sharrigan',
    collections: [
      {
        name: 'Dragonkin I', requiredLevel: 99, collector: 'Sharrigan',
        artefacts: [
          { name: 'Xolo mask', wikiUrl: 'https://runescape.wiki/w/Xolo_mask' },
          { name: 'Dragonkin tablet', wikiUrl: 'https://runescape.wiki/w/Dragonkin_tablet' },
          { name: 'Dragonkin journal', wikiUrl: 'https://runescape.wiki/w/Dragonkin_journal' },
          { name: 'Dragonkin research notes', wikiUrl: 'https://runescape.wiki/w/Dragonkin_research_notes' },
          { name: 'Dragonkin canopic jar', wikiUrl: 'https://runescape.wiki/w/Dragonkin_canopic_jar' },
          { name: 'Orthen map piece', wikiUrl: 'https://runescape.wiki/w/Orthen_map_piece' },
          { name: 'Xolo ring', wikiUrl: 'https://runescape.wiki/w/Xolo_ring' },
        ],
      },
      {
        name: 'Dragonkin II', requiredLevel: 101, collector: 'Sharrigan',
        artefacts: [
          { name: 'Dragonkin plague notes', wikiUrl: 'https://runescape.wiki/w/Dragonkin_plague_notes' },
          { name: 'Dragonkin device', wikiUrl: 'https://runescape.wiki/w/Dragonkin_device' },
          { name: 'Orthen furnace core', wikiUrl: 'https://runescape.wiki/w/Orthen_furnace_core' },
          { name: "Kerapac's wristbands", wikiUrl: "https://runescape.wiki/w/Kerapac's_wristbands" },
          { name: "Dagon's mark", wikiUrl: "https://runescape.wiki/w/Dagon's_mark" },
        ],
      },
      {
        name: 'Dragonkin III', requiredLevel: 104, collector: 'Sharrigan',
        artefacts: [
          { name: 'Dragonkin war axe', wikiUrl: 'https://runescape.wiki/w/Dragonkin_war_axe' },
          { name: 'Dragonkin memory', wikiUrl: 'https://runescape.wiki/w/Dragonkin_memory' },
          { name: 'Oathplate', wikiUrl: 'https://runescape.wiki/w/Oathplate' },
          { name: 'Remnant of a god', wikiUrl: 'https://runescape.wiki/w/Remnant_of_a_god' },
        ],
      },
      {
        name: 'Dragonkin IV', requiredLevel: 107, collector: 'Sharrigan',
        artefacts: [
          { name: 'Elder Sword piece', wikiUrl: 'https://runescape.wiki/w/Elder_Sword_piece' },
          { name: 'Elder Wand piece', wikiUrl: 'https://runescape.wiki/w/Elder_Wand_piece' },
          { name: 'Orthen teleportation device', wikiUrl: 'https://runescape.wiki/w/Orthen_teleportation_device' },
          { name: 'Staff of Armadyl (artefact)', wikiUrl: 'https://runescape.wiki/w/Staff_of_Armadyl_(artefact)' },
        ],
      },
      {
        name: 'Dragonkin V', requiredLevel: 77, collector: 'Sharrigan',
        artefacts: [
          { name: 'Goebie carving', wikiUrl: 'https://runescape.wiki/w/Goebie_carving' },
          { name: 'Kanatah figurine', wikiUrl: 'https://runescape.wiki/w/Kanatah_figurine' },
          { name: 'Rex Matriarch claw', wikiUrl: 'https://runescape.wiki/w/Rex_Matriarch_claw' },
          { name: 'Saurthen debris', wikiUrl: 'https://runescape.wiki/w/Saurthen_debris' },
        ],
      },
      {
        name: 'Dragonkin VI', requiredLevel: 87, collector: 'Sharrigan',
        artefacts: [
          { name: 'Bandosian weapon fragment', wikiUrl: 'https://runescape.wiki/w/Bandosian_weapon_fragment' },
          { name: 'Garagorshuun war hammer', wikiUrl: 'https://runescape.wiki/w/Garagorshuun_war_hammer' },
          { name: 'Ourg bone', wikiUrl: 'https://runescape.wiki/w/Ourg_bone' },
          { name: 'Ourg megahitter', wikiUrl: 'https://runescape.wiki/w/Ourg_megahitter' },
          { name: 'Ourg prayer beads', wikiUrl: 'https://runescape.wiki/w/Ourg_prayer_beads' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
        ],
      },
      {
        name: 'Dragonkin VII', requiredLevel: 109, collector: 'Sharrigan',
        artefacts: [
          { name: 'Ancient zygomite fossil', wikiUrl: 'https://runescape.wiki/w/Ancient_zygomite_fossil' },
          { name: 'Fossilised ent', wikiUrl: 'https://runescape.wiki/w/Fossilised_ent' },
          { name: 'Orthen cache key', wikiUrl: 'https://runescape.wiki/w/Orthen_cache_key' },
          { name: 'Preserved cave horror', wikiUrl: 'https://runescape.wiki/w/Preserved_cave_horror' },
          { name: 'Xolo idol', wikiUrl: 'https://runescape.wiki/w/Xolo_idol' },
        ],
      },
    ],
  },
  {
    name: 'General Bentnoze',
    collections: [
      {
        name: 'Red Rum Relics I', requiredLevel: 89, collector: 'General Bentnoze',
        artefacts: [
          { name: 'Bandosian weapon fragment', wikiUrl: 'https://runescape.wiki/w/Bandosian_weapon_fragment' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: 'Warforge pig iron', wikiUrl: 'https://runescape.wiki/w/Warforge_pig_iron' },
          { name: "Yu'biusk clay", wikiUrl: "https://runescape.wiki/w/Yu'biusk_clay" },
        ],
      },
      {
        name: 'Red Rum Relics II', requiredLevel: 97, collector: 'General Bentnoze',
        artefacts: [
          { name: 'Garagorshuun war hammer', wikiUrl: 'https://runescape.wiki/w/Garagorshuun_war_hammer' },
          { name: 'Ourg bone', wikiUrl: 'https://runescape.wiki/w/Ourg_bone' },
          { name: 'Ourg megahitter', wikiUrl: 'https://runescape.wiki/w/Ourg_megahitter' },
          { name: 'Ourg prayer beads', wikiUrl: 'https://runescape.wiki/w/Ourg_prayer_beads' },
          { name: "Dorgeshuun c'bow", wikiUrl: "https://runescape.wiki/w/Dorgeshuun_c'bow_(artefact)" },
        ],
      },
      {
        name: 'Red Rum Relics III', requiredLevel: 109, collector: 'General Bentnoze',
        artefacts: [
          { name: 'Ancient zygomite fossil', wikiUrl: 'https://runescape.wiki/w/Ancient_zygomite_fossil' },
          { name: 'Fossilised ent', wikiUrl: 'https://runescape.wiki/w/Fossilised_ent' },
          { name: 'Preserved cave horror', wikiUrl: 'https://runescape.wiki/w/Preserved_cave_horror' },
          { name: 'Xolo idol', wikiUrl: 'https://runescape.wiki/w/Xolo_idol' },
          { name: 'Orthen cache key', wikiUrl: 'https://runescape.wiki/w/Orthen_cache_key' },
        ],
      },
    ],
  },
  {
    name: 'General Wartface',
    collections: [
      {
        name: 'Green Gobbo Goodies I', requiredLevel: 83, collector: 'General Wartface',
        artefacts: [
          { name: 'Bandosian weapon fragment', wikiUrl: 'https://runescape.wiki/w/Bandosian_weapon_fragment' },
          { name: 'Garagorshuun war hammer', wikiUrl: 'https://runescape.wiki/w/Garagorshuun_war_hammer' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: 'Warforge pig iron', wikiUrl: 'https://runescape.wiki/w/Warforge_pig_iron' },
          { name: "Yu'biusk clay", wikiUrl: "https://runescape.wiki/w/Yu'biusk_clay" },
        ],
      },
      {
        name: 'Green Gobbo Goodies II', requiredLevel: 97, collector: 'General Wartface',
        artefacts: [
          { name: 'Ourg bone', wikiUrl: 'https://runescape.wiki/w/Ourg_bone' },
          { name: 'Ourg megahitter', wikiUrl: 'https://runescape.wiki/w/Ourg_megahitter' },
          { name: 'Ourg prayer beads', wikiUrl: 'https://runescape.wiki/w/Ourg_prayer_beads' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: "Dorgeshuun c'bow", wikiUrl: "https://runescape.wiki/w/Dorgeshuun_c'bow_(artefact)" },
        ],
      },
      {
        name: 'Green Gobbo Goodies III', requiredLevel: 109, collector: 'General Wartface',
        artefacts: [
          { name: 'Ancient zygomite fossil', wikiUrl: 'https://runescape.wiki/w/Ancient_zygomite_fossil' },
          { name: 'Fossilised ent', wikiUrl: 'https://runescape.wiki/w/Fossilised_ent' },
          { name: 'Preserved cave horror', wikiUrl: 'https://runescape.wiki/w/Preserved_cave_horror' },
          { name: 'Xolo idol', wikiUrl: 'https://runescape.wiki/w/Xolo_idol' },
        ],
      },
    ],
  },
  {
    name: 'Chief Tess',
    collections: [
      {
        name: 'Blingy Fings', requiredLevel: 47, collector: 'Chief Tess',
        artefacts: [
          { name: 'Ancient timepiece', wikiUrl: 'https://runescape.wiki/w/Ancient_timepiece' },
          { name: 'Legatus pendant', wikiUrl: 'https://runescape.wiki/w/Legatus_pendant' },
          { name: 'Pontifex signet ring', wikiUrl: 'https://runescape.wiki/w/Pontifex_signet_ring' },
          { name: 'Dominion discus', wikiUrl: 'https://runescape.wiki/w/Dominion_discus' },
          { name: 'Icyenic pendant', wikiUrl: 'https://runescape.wiki/w/Icyenic_pendant' },
        ],
      },
      {
        name: 'Smoky Fings', requiredLevel: 20, collector: 'Chief Tess',
        artefacts: [
          { name: 'Hookah pipe', wikiUrl: 'https://runescape.wiki/w/Hookah_pipe' },
          { name: 'Opulent wine goblet', wikiUrl: 'https://runescape.wiki/w/Opulent_wine_goblet' },
          { name: 'Everlight trumpet', wikiUrl: 'https://runescape.wiki/w/Everlight_trumpet' },
          { name: 'Soul urn', wikiUrl: 'https://runescape.wiki/w/Soul_urn' },
          { name: 'Infernal art', wikiUrl: 'https://runescape.wiki/w/Infernal_art' },
        ],
      },
      {
        name: 'Hitty Fings', requiredLevel: 83, collector: 'Chief Tess',
        artefacts: [
          { name: 'Bandosian weapon fragment', wikiUrl: 'https://runescape.wiki/w/Bandosian_weapon_fragment' },
          { name: 'Garagorshuun war hammer', wikiUrl: 'https://runescape.wiki/w/Garagorshuun_war_hammer' },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: 'Warforge pig iron', wikiUrl: 'https://runescape.wiki/w/Warforge_pig_iron' },
          { name: "Yu'biusk clay", wikiUrl: "https://runescape.wiki/w/Yu'biusk_clay" },
        ],
      },
      {
        name: 'Showy Fings', requiredLevel: 24, collector: 'Chief Tess',
        artefacts: [
          { name: 'Crest of Dagon', wikiUrl: 'https://runescape.wiki/w/Crest_of_Dagon' },
          { name: 'Legatus Maximus figurine', wikiUrl: 'https://runescape.wiki/w/Legatus_Maximus_figurine' },
          { name: 'Dominion javelin', wikiUrl: 'https://runescape.wiki/w/Dominion_javelin' },
          { name: 'Dominion sword', wikiUrl: 'https://runescape.wiki/w/Dominion_sword' },
          { name: 'Funeral urn (Saradominist)', wikiUrl: 'https://runescape.wiki/w/Funeral_urn_(Saradominist)' },
          { name: 'Icyenic armour fragment', wikiUrl: 'https://runescape.wiki/w/Icyenic_armour_fragment' },
        ],
      },
    ],
  },
  {
    name: 'Eblis',
    collections: [
      {
        name: 'Finery of the Inquisition', requiredLevel: 60, collector: 'Eblis',
        artefacts: [
          { name: 'Apex cap', wikiUrl: 'https://runescape.wiki/w/Apex_cap' },
          { name: 'Curse tablet', wikiUrl: 'https://runescape.wiki/w/Curse_tablet' },
          { name: 'Funerary urn of shadow', wikiUrl: 'https://runescape.wiki/w/Funerary_urn_of_shadow' },
        ],
      },
      {
        name: 'Religious Iconography', requiredLevel: 58, collector: 'Eblis',
        artefacts: [
          { name: 'Pontifex signet ring', wikiUrl: 'https://runescape.wiki/w/Pontifex_signet_ring' },
          { name: "'Incite Fear' spell scroll", wikiUrl: "https://runescape.wiki/w/'Incite_Fear'_spell_scroll" },
          { name: 'Apex cap', wikiUrl: 'https://runescape.wiki/w/Apex_cap' },
          { name: 'Curse tablet', wikiUrl: 'https://runescape.wiki/w/Curse_tablet' },
        ],
      },
      {
        name: 'Urns of the Empire', requiredLevel: 60, collector: 'Eblis',
        artefacts: [
          { name: 'Funerary urn of shadow', wikiUrl: 'https://runescape.wiki/w/Funerary_urn_of_shadow' },
          { name: 'Amphitheatre debris', wikiUrl: 'https://runescape.wiki/w/Amphitheatre_debris' },
          { name: 'Castra debris', wikiUrl: 'https://runescape.wiki/w/Castra_debris' },
          { name: 'Praetorium debris', wikiUrl: 'https://runescape.wiki/w/Praetorium_debris' },
          { name: 'Triumphal arch debris', wikiUrl: 'https://runescape.wiki/w/Triumphal_arch_debris' },
        ],
      },
      {
        name: 'Entertaining the Masses', requiredLevel: 61, collector: 'Eblis',
        artefacts: [
          { name: 'Dominion discus', wikiUrl: 'https://runescape.wiki/w/Dominion_discus' },
          { name: 'Dominion javelin', wikiUrl: 'https://runescape.wiki/w/Dominion_javelin' },
          { name: 'Dominion sword', wikiUrl: 'https://runescape.wiki/w/Dominion_sword' },
          { name: 'Icyenic bow', wikiUrl: 'https://runescape.wiki/w/Icyenic_bow' },
        ],
      },
      {
        name: 'Imperial Sorcery', requiredLevel: 58, collector: 'Eblis',
        artefacts: [
          { name: "'Incite Fear' spell scroll", wikiUrl: "https://runescape.wiki/w/'Incite_Fear'_spell_scroll" },
          { name: 'Curse tablet', wikiUrl: 'https://runescape.wiki/w/Curse_tablet' },
          { name: 'War table debris', wikiUrl: 'https://runescape.wiki/w/War_table_debris' },
          { name: 'Shadow weapon rack', wikiUrl: 'https://runescape.wiki/w/Shadow_weapon_rack' },
        ],
      },
    ],
  },
  {
    name: 'Wise Old Man',
    collections: [
      {
        name: 'Wise Am the Music Man', requiredLevel: 51, collector: 'Wise Old Man',
        artefacts: [
          { name: 'Everlight harp', wikiUrl: 'https://runescape.wiki/w/Everlight_harp' },
          { name: 'Everlight trumpet', wikiUrl: 'https://runescape.wiki/w/Everlight_trumpet' },
          { name: 'Everlight violin', wikiUrl: 'https://runescape.wiki/w/Everlight_violin' },
          { name: 'Dominion discus', wikiUrl: 'https://runescape.wiki/w/Dominion_discus' },
          { name: 'Icyenic bow', wikiUrl: 'https://runescape.wiki/w/Icyenic_bow' },
          { name: 'Ceremonial unicorn ornament', wikiUrl: 'https://runescape.wiki/w/Ceremonial_unicorn_ornament' },
          { name: 'Ceremonial unicorn saddle', wikiUrl: 'https://runescape.wiki/w/Ceremonial_unicorn_saddle' },
        ],
      },
      {
        name: 'Hat Problem', requiredLevel: 29, collector: 'Wise Old Man',
        artefacts: [
          { name: 'Imp mask', wikiUrl: 'https://runescape.wiki/w/Imp_mask' },
          { name: 'Lesser demon mask', wikiUrl: 'https://runescape.wiki/w/Lesser_demon_mask' },
          { name: 'Greater demon mask', wikiUrl: 'https://runescape.wiki/w/Greater_demon_mask' },
          { name: 'Ceremonial unicorn ornament', wikiUrl: 'https://runescape.wiki/w/Ceremonial_unicorn_ornament' },
          { name: 'Folded-arm figurine (female)', wikiUrl: 'https://runescape.wiki/w/Folded-arm_figurine_(female)' },
          { name: 'Folded-arm figurine (male)', wikiUrl: 'https://runescape.wiki/w/Folded-arm_figurine_(male)' },
          { name: 'Apex cap', wikiUrl: 'https://runescape.wiki/w/Apex_cap' },
        ],
      },
      {
        name: 'Hat Hoarder', requiredLevel: 48, collector: 'Wise Old Man',
        artefacts: [
          { name: 'Ceremonial unicorn ornament', wikiUrl: 'https://runescape.wiki/w/Ceremonial_unicorn_ornament' },
          { name: 'Lesser demon mask', wikiUrl: 'https://runescape.wiki/w/Lesser_demon_mask' },
          { name: 'Greater demon mask', wikiUrl: 'https://runescape.wiki/w/Greater_demon_mask' },
          { name: 'Folded-arm figurine (female)', wikiUrl: 'https://runescape.wiki/w/Folded-arm_figurine_(female)' },
          { name: 'Folded-arm figurine (male)', wikiUrl: 'https://runescape.wiki/w/Folded-arm_figurine_(male)' },
          { name: 'Icyenic defence scroll', wikiUrl: 'https://runescape.wiki/w/Icyenic_defence_scroll' },
          { name: 'Icyenic prayer scroll', wikiUrl: 'https://runescape.wiki/w/Icyenic_prayer_scroll' },
        ],
      },
      {
        name: 'Magic Man', requiredLevel: 25, collector: 'Wise Old Man',
        artefacts: [
          { name: 'Legatus Maximus figurine', wikiUrl: 'https://runescape.wiki/w/Legatus_Maximus_figurine' },
          { name: 'Ritual dagger', wikiUrl: 'https://runescape.wiki/w/Ritual_dagger' },
          { name: 'Ancient timepiece', wikiUrl: 'https://runescape.wiki/w/Ancient_timepiece' },
          { name: "'Incite Fear' spell scroll", wikiUrl: "https://runescape.wiki/w/'Incite_Fear'_spell_scroll" },
          { name: 'Crest of Dagon', wikiUrl: 'https://runescape.wiki/w/Crest_of_Dagon' },
        ],
      },
      {
        name: 'Knowledge is Power', requiredLevel: 113, collector: 'Wise Old Man',
        artefacts: [
          { name: 'Aviansie dreamcoat', wikiUrl: 'https://runescape.wiki/w/Aviansie_dreamcoat' },
          { name: 'Flight recorder', wikiUrl: 'https://runescape.wiki/w/Flight_recorder' },
          { name: 'Hurricane spin top', wikiUrl: 'https://runescape.wiki/w/Hurricane_spin_top' },
          { name: 'Omnidirectional dome', wikiUrl: 'https://runescape.wiki/w/Omnidirectional_dome' },
          { name: 'Prototype gravimetric generator', wikiUrl: 'https://runescape.wiki/w/Prototype_gravimetric_generator' },
        ],
      },
    ],
  },
  {
    name: 'Art Critic Jacques',
    collections: [
      {
        name: 'Anarchic Abstraction', requiredLevel: 24, collector: 'Art Critic Jacques',
        artefacts: [
          { name: "'Disorder' painting", wikiUrl: "https://runescape.wiki/w/'Disorder'_painting" },
          { name: 'Ork cleaver sword', wikiUrl: 'https://runescape.wiki/w/Ork_cleaver_sword' },
          { name: 'Warforge pig iron', wikiUrl: 'https://runescape.wiki/w/Warforge_pig_iron' },
        ],
      },
      {
        name: 'Radiant Renaissance', requiredLevel: 81, collector: 'Art Critic Jacques',
        artefacts: [
          { name: 'Armadylean short bow', wikiUrl: 'https://runescape.wiki/w/Armadylean_short_bow' },
          { name: 'Armadylean cloak', wikiUrl: 'https://runescape.wiki/w/Armadylean_cloak' },
          { name: 'Armadylean body', wikiUrl: 'https://runescape.wiki/w/Armadylean_body' },
          { name: 'Armadylean warstaff', wikiUrl: 'https://runescape.wiki/w/Armadylean_warstaff' },
        ],
      },
      {
        name: 'Imperial Impressionism', requiredLevel: 25, collector: 'Art Critic Jacques',
        artefacts: [
          { name: "'Solem in Umbra' painting", wikiUrl: "https://runescape.wiki/w/'Solem_in_Umbra'_painting" },
          { name: 'Legatus Maximus figurine', wikiUrl: 'https://runescape.wiki/w/Legatus_Maximus_figurine' },
          { name: 'Dominion javelin', wikiUrl: 'https://runescape.wiki/w/Dominion_javelin' },
          { name: 'Dominion sword', wikiUrl: 'https://runescape.wiki/w/Dominion_sword' },
          { name: 'Funeral urn (Saradominist)', wikiUrl: 'https://runescape.wiki/w/Funeral_urn_(Saradominist)' },
        ],
      },
    ],
  },
]
