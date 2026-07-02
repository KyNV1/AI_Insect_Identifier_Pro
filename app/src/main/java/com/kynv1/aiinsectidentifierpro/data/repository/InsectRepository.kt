package com.kynv1.aiinsectidentifierpro.data.repository

import android.graphics.Bitmap
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.data.local.InsectDao
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import com.kynv1.aiinsectidentifierpro.data.model.InsectShort
import com.kynv1.aiinsectidentifierpro.data.model.HomeArticle
import com.kynv1.aiinsectidentifierpro.data.remote.GeminiServiceClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray

class InsectRepository(
    private val insectDao: InsectDao,
    private val geminiServiceClient: GeminiServiceClient = GeminiServiceClient()
) {
    val allInsectsFlow: Flow<List<InsectEntity>> = insectDao.getAllInsectsFlow()

    fun getMockInsects(): List<InsectShort> {
        return listOf(
            // Most Common
            InsectShort(
                id = 10001L,
                commonName = "Ash-black Slug",
                scientificName = "Limax cinereoniger",
                imageResId = R.drawable.img_onboarding_green_beetle,
                category = "Most Common"
            ),
            InsectShort(
                id = 10002L,
                commonName = "Black Oil Beetle",
                scientificName = "Meloe proscarabaeus",
                imageResId = R.drawable.img_onboarding_red_beetle,
                category = "Most Common"
            ),
            InsectShort(
                id = 10003L,
                commonName = "Broom-tip Moth",
                scientificName = "Chesias rufata",
                imageResId = R.drawable.img_onboarding_atlas_moth,
                category = "Most Common"
            ),
            InsectShort(
                id = 10004L,
                commonName = "Buffish Mining Bee",
                scientificName = "Andrena fulva",
                imageResId = R.drawable.img_onboarding_honey_bee,
                category = "Most Common"
            ),
            InsectShort(
                id = 10005L,
                commonName = "Common Wasp",
                scientificName = "Vespula vulgaris",
                imageResId = R.drawable.img_onboarding_honey_bee,
                category = "Most Common"
            ),

            // Garden Insect
            InsectShort(
                id = 10006L,
                commonName = "Brown-lipped Snail",
                scientificName = "Cepaea nemoralis",
                imageResId = R.drawable.img_onboarding_green_beetle,
                category = "Garden Insect"
            ),
            InsectShort(
                id = 10007L,
                commonName = "Black and red froghopper",
                scientificName = "Cercopis vulnerata",
                imageResId = R.drawable.img_onboarding_red_beetle,
                category = "Garden Insect"
            ),
            InsectShort(
                id = 10008L,
                commonName = "The Sabre Wasp",
                scientificName = "Rhyssa persuasoria",
                imageResId = R.drawable.img_onboarding_atlas_moth,
                category = "Garden Insect"
            ),
            InsectShort(
                id = 10009L,
                commonName = "Honey Bee",
                scientificName = "Apis mellifera",
                imageResId = R.drawable.img_onboarding_honey_bee,
                category = "Garden Insect"
            ),
            InsectShort(
                id = 10010L,
                commonName = "Red Ladybug",
                scientificName = "Harmonia axyridis",
                imageResId = R.drawable.img_onboarding_red_beetle,
                category = "Garden Insect"
            )
        )
    }

    fun getMockArticles(): List<HomeArticle> {
        return listOf(
            // Fun Bug Facts
            HomeArticle(
                id = 20001L,
                title = "What? Now ticks can fly?!",
                imageResId = R.drawable.img_onboarding_honey_bee,
                category = "Fun Bug Facts"
            ),
            HomeArticle(
                id = 20002L,
                title = "Where Do Termites Hide?",
                imageResId = R.drawable.img_onboarding_red_beetle,
                category = "Fun Bug Facts"
            ),
            HomeArticle(
                id = 20003L,
                title = "What Do Pill Bugs Eat?",
                imageResId = R.drawable.img_onboarding_green_beetle,
                category = "Fun Bug Facts"
            ),

            // Pest Control
            HomeArticle(
                id = 20004L,
                title = "How to Prevent Unwanted Kitchen Pests",
                imageResId = R.drawable.img_onboarding_atlas_moth,
                category = "Pest Control"
            ),
            HomeArticle(
                id = 20005L,
                title = "How to Get Rid of Boxelder Bugs",
                imageResId = R.drawable.img_onboarding_honey_bee,
                category = "Pest Control"
            ),
            HomeArticle(
                id = 20006L,
                title = "How to Get Rid of Mice",
                imageResId = R.drawable.img_onboarding_red_beetle,
                category = "Pest Control"
            ),

            // Bug Bite Help
            HomeArticle(
                id = 20007L,
                title = "How to Treat a Spider Bite",
                imageResId = R.drawable.img_onboarding_green_beetle,
                category = "Bug Bite Help"
            ),
            HomeArticle(
                id = 20008L,
                title = "Tips for Treating Insect Stings",
                imageResId = R.drawable.img_onboarding_atlas_moth,
                category = "Bug Bite Help"
            ),
            HomeArticle(
                id = 20009L,
                title = "Flea Bites",
                imageResId = R.drawable.img_onboarding_honey_bee,
                category = "Bug Bite Help"
            ),

            // Remarkable Collection
            HomeArticle(
                id = 20010L,
                title = "Creating an Insect Collection",
                imageResId = R.drawable.img_onboarding_red_beetle,
                category = "Remarkable Collection"
            ),
            HomeArticle(
                id = 20011L,
                title = "The John Landy Butterfly Collection",
                imageResId = R.drawable.img_onboarding_green_beetle,
                category = "Remarkable Collection"
            ),
            HomeArticle(
                id = 20012L,
                title = "The Scott Sisters Collection",
                imageResId = R.drawable.img_onboarding_atlas_moth,
                category = "Remarkable Collection"
            )
        )
    }

    suspend fun getInsectById(id: Long): InsectEntity? {
        if (id in 10001L..10010L || id in 20001L..20012L) {
            return getStaticInsectEntity(id)
        }
        return insectDao.getInsectById(id)
    }

    private fun getStaticInsectEntity(id: Long): InsectEntity {
        val packageName = "com.kynv1.aiinsectidentifierpro"
        return when (id) {
            10001L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_green_beetle}",
                commonName = "Ash-black Slug",
                scientificName = "Limax cinereoniger",
                confidence = 97,
                description = "The ash-black slug (Limax cinereoniger) is the largest species of land slug in the world. It is an air-breathing land gastropod mollusk in the family Limacidae.",
                characteristicsJson = JSONArray(listOf("Largest land slug", "Air-breathing", "Feeds on fungi and algae")).toString(),
                habitat = "Old coniferous and deciduous forests",
                dangerLevel = "Low",
                dangerDescription = "Completely harmless to humans, plays an important role as a decomposer.",
                timestamp = System.currentTimeMillis()
            )
            10002L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_red_beetle}",
                commonName = "Black Oil Beetle",
                scientificName = "Meloe proscarabaeus",
                confidence = 94,
                description = "The black oil beetle (Meloe proscarabaeus) is a species of oil beetle. It has a large, soft body and can secrete oily droplets of hemolymph containing cantharidin when threatened.",
                characteristicsJson = JSONArray(listOf("Secretes oily hemolymph", "Parasitic larvae", "Flightless beetle")).toString(),
                habitat = "Grassy areas, woodlands, and flower fields",
                dangerLevel = "Medium",
                dangerDescription = "Can cause skin irritation or blistering if handled roughly, due to the cantharidin toxin they secrete.",
                timestamp = System.currentTimeMillis()
            )
            10003L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_atlas_moth}",
                commonName = "Broom-tip Moth",
                scientificName = "Chesias rufata",
                confidence = 96,
                description = "The broom-tip (Chesias rufata) is a moth of the family Geometridae. The species is found throughout Europe and North Africa, and its larvae feed primarily on broom plants.",
                characteristicsJson = JSONArray(listOf("Night-flying", "Camouflaged wings", "Feeds on broom shrubs")).toString(),
                habitat = "Heathlands, sandy hills, and dry valleys",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans and pets.",
                timestamp = System.currentTimeMillis()
            )
            10004L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_honey_bee}",
                commonName = "Buffish Mining Bee",
                scientificName = "Andrena fulva",
                confidence = 95,
                description = "The tawny mining bee (Andrena fulva) is a common European species of mining bee. They are solitary bees, nesting in small tunnels dug directly into lawns and garden soils.",
                characteristicsJson = JSONArray(listOf("Solitary nester", "Tawny orange coat", "Excellent pollinator")).toString(),
                habitat = "Gardens, parks, lawns, and meadows",
                dangerLevel = "Low",
                dangerDescription = "Non-aggressive and rarely stings. If stung, symptoms are minor unless allergic.",
                timestamp = System.currentTimeMillis()
            )
            10005L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_honey_bee}",
                commonName = "Common Wasp",
                scientificName = "Vespula vulgaris",
                confidence = 98,
                description = "The common wasp (Vespula vulgaris) is a social wasp species found in the Northern Hemisphere. They live in large colonies with a queen and worker wasps, nesting underground or in cavities.",
                characteristicsJson = JSONArray(listOf("Social insect", "Paper-like nests", "Predatory on pests")).toString(),
                habitat = "Woodlands, gardens, orchards, and urban areas",
                dangerLevel = "High",
                dangerDescription = "Aggressive when defending nests. Stings are painful and can cause severe allergic reactions (anaphylaxis) in sensitive individuals.",
                timestamp = System.currentTimeMillis()
            )
            10006L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_green_beetle}",
                commonName = "Brown-lipped Snail",
                scientificName = "Cepaea nemoralis",
                confidence = 93,
                description = "The brown-lipped snail (Cepaea nemoralis) is a highly variable land snail. It is one of the most common species of land snails in Europe and has been introduced to North America.",
                characteristicsJson = JSONArray(listOf("Variable shell color", "Dark brown lip rim", "Herbivorous diet")).toString(),
                habitat = "Gardens, sand dunes, and hedgerows",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans, but can be a minor pest to young garden plants.",
                timestamp = System.currentTimeMillis()
            )
            10007L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_red_beetle}",
                commonName = "Black and red froghopper",
                scientificName = "Cercopis vulnerata",
                confidence = 95,
                description = "The red-and-black froghopper (Cercopis vulnerata) is a species of froghopper common in Europe. It is easily recognized by its bold black and red warning coloration.",
                characteristicsJson = JSONArray(listOf("Bold warning colors", "Nymphs make spit-like nests", "Herbivorous sap sucker")).toString(),
                habitat = "Grassy meadows, forest edges, and gardens",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans and animals.",
                timestamp = System.currentTimeMillis()
            )
            10008L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_atlas_moth}",
                commonName = "The Sabre Wasp",
                scientificName = "Rhyssa persuasoria",
                confidence = 96,
                description = "The sabre wasp (Rhyssa persuasoria) is one of the largest ichneumon wasps in Europe. The female has an exceptionally long ovipositor, resembling a sabre, used to lay eggs on larvae inside wood.",
                characteristicsJson = JSONArray(listOf("Extremely long ovipositor", "Parasitic behavior", "Harmless to humans")).toString(),
                habitat = "Coniferous forests and woodlands",
                dangerLevel = "Low",
                dangerDescription = "Completely harmless to humans. The long ovipositor cannot sting humans.",
                timestamp = System.currentTimeMillis()
            )
            10009L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_honey_bee}",
                commonName = "Honey Bee",
                scientificName = "Apis mellifera",
                confidence = 98,
                description = "The western honey bee or European honey bee (Apis mellifera) is the most common of the 7-12 species of honey bees worldwide. They are highly social insects living in colonies.",
                characteristicsJson = JSONArray(listOf("Social insect", "Produces honey", "Important pollinator")).toString(),
                habitat = "Gardens, woodlands, orchards, and meadows",
                dangerLevel = "Low",
                dangerDescription = "Honey bees are generally non-aggressive and will only sting if they feel threatened or to defend their hive. Stings can cause allergic reactions in sensitive individuals.",
                timestamp = System.currentTimeMillis()
            )
            10010L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_red_beetle}",
                commonName = "Red Ladybug",
                scientificName = "Harmonia axyridis",
                confidence = 95,
                description = "The Asian ladybeetle (Harmonia axyridis) is a large coccinellid beetle. It is one of the most variable species in the world, with an exceptionally wide range of color forms.",
                characteristicsJson = JSONArray(listOf("Voracious predator of aphids", "Variable coloration", "Can aggregate in large numbers")).toString(),
                habitat = "Agricultural fields, gardens, and forests",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans, though they can bite occasionally and produce a smelly yellow fluid when threatened.",
                timestamp = System.currentTimeMillis()
            )
            // Articles Mapping
            20001L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_honey_bee}",
                commonName = "What? Now ticks can fly?!",
                scientificName = "Fun Bug Facts",
                confidence = 100,
                description = "Recent entomological studies have debunked the myth that ticks are completely ground-bound. While they don't possess wings, they can use wind currents or electrostatic forces to glide over short distances.",
                characteristicsJson = JSONArray(listOf("No wings", "Electrostatic gliding", "Wind assistance")).toString(),
                habitat = "Tall grass, shrubs, and wind currents",
                dangerLevel = "Info",
                dangerDescription = "Informational article. Always verify tick bites with a physician.",
                timestamp = System.currentTimeMillis()
            )
            20002L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_red_beetle}",
                commonName = "Where Do Termites Hide?",
                scientificName = "Fun Bug Facts",
                confidence = 100,
                description = "Termites are masters of stealth. They build underground colonies, nests inside walls, or deep inside decaying wood, making them extremely hard to detect until structural damage is done.",
                characteristicsJson = JSONArray(listOf("Underground nests", "Stealthy behavior", "Cellulose eaters")).toString(),
                habitat = "Decaying wood, structural foundations",
                dangerLevel = "Info",
                dangerDescription = "Informational guide about termite nesting habits.",
                timestamp = System.currentTimeMillis()
            )
            20003L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_green_beetle}",
                commonName = "What Do Pill Bugs Eat?",
                scientificName = "Fun Bug Facts",
                confidence = 100,
                description = "Pill bugs (also known as roly-polies) are actually terrestrial isopods, not insects! They decompose dead plant matter, recycling vital nutrients back into the soil.",
                characteristicsJson = JSONArray(listOf("Terrestrial crustacean", "Decomposers", "Roll into a ball")).toString(),
                habitat = "Moist soil, organic leaf litter",
                dangerLevel = "Info",
                dangerDescription = "Informational guide about pill bug diet and ecosystems.",
                timestamp = System.currentTimeMillis()
            )
            20004L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_atlas_moth}",
                commonName = "How to Prevent Unwanted Kitchen Pests",
                scientificName = "Pest Control",
                confidence = 100,
                description = "Prevention is key. Keep food sealed in airtight containers, sweep crumbs daily, keep sinks dry, and seal any cracks under doors or windows.",
                characteristicsJson = JSONArray(listOf("Food hygiene", "Seal entry points", "Airtight storage")).toString(),
                habitat = "Kitchens, pantries, food storage areas",
                dangerLevel = "Info",
                dangerDescription = "Actionable strategies for home pest prevention.",
                timestamp = System.currentTimeMillis()
            )
            20005L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_honey_bee}",
                commonName = "How to Get Rid of Boxelder Bugs",
                scientificName = "Pest Control",
                confidence = 100,
                description = "Use soapy water sprays, vacuum any indoor bugs directly, and apply natural deterrents like diatomaceous earth around entryways.",
                characteristicsJson = JSONArray(listOf("Soapy water spray", "Physical removal", "Diatomaceous earth")).toString(),
                habitat = "Windows, entryways, sunny walls",
                dangerLevel = "Info",
                dangerDescription = "Safe and organic ways to manage Boxelder bug infestations.",
                timestamp = System.currentTimeMillis()
            )
            20006L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_red_beetle}",
                commonName = "How to Get Rid of Mice",
                scientificName = "Pest Control",
                confidence = 100,
                description = "Keep food sources closed, set humane traps, plug holes with steel wool (which mice cannot chew through), and use natural repellents like peppermint oil.",
                characteristicsJson = JSONArray(listOf("Steel wool plugs", "Humane traps", "Peppermint oil scent")).toString(),
                habitat = "Basements, attics, wall crevices",
                dangerLevel = "Info",
                dangerDescription = "Effective home mouse control guide.",
                timestamp = System.currentTimeMillis()
            )
            20007L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_green_beetle}",
                commonName = "How to Treat a Spider Bite",
                scientificName = "Bug Bite Help",
                confidence = 100,
                description = "Wash the bite with soap and water, apply a cold compress to reduce swelling, keep the area elevated, and seek medical attention if symptoms worsen.",
                characteristicsJson = JSONArray(listOf("Wash immediately", "Cold compress", "Monitor symptoms")).toString(),
                habitat = "First aid, home care",
                dangerLevel = "Info",
                dangerDescription = "First-aid guidance for spider bites.",
                timestamp = System.currentTimeMillis()
            )
            20008L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_atlas_moth}",
                commonName = "Tips for Treating Insect Stings",
                scientificName = "Bug Bite Help",
                confidence = 100,
                description = "Remove the stinger immediately using a flat edge, apply ice, take antihistamines if needed, and watch closely for signs of severe allergic reactions (anaphylaxis).",
                characteristicsJson = JSONArray(listOf("Stinger removal", "Apply ice pack", "Allergy watch")).toString(),
                habitat = "First aid, emergency care",
                dangerLevel = "Info",
                dangerDescription = "Critical advice for managing bee and wasp stings.",
                timestamp = System.currentTimeMillis()
            )
            20009L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_honey_bee}",
                commonName = "Flea Bites",
                scientificName = "Bug Bite Help",
                confidence = 100,
                description = "Flea bites usually appear in clusters of small red bumps, typically around ankles. Wash the area, avoid scratching to prevent infection, and treat your pets immediately.",
                characteristicsJson = JSONArray(listOf("Red bumps clusters", "Avoid scratching", "Treat pets")).toString(),
                habitat = "Ankles, legs, pet nesting areas",
                dangerLevel = "Info",
                dangerDescription = "Guidance on treating flea bites and pet hygiene.",
                timestamp = System.currentTimeMillis()
            )
            20010L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_red_beetle}",
                commonName = "Creating an Insect Collection",
                scientificName = "Remarkable Collection",
                confidence = 100,
                description = "Learn the proper techniques for collecting, preserving, and mounting insects for scientific study or personal hobby. Respect nature and only collect ethically.",
                characteristicsJson = JSONArray(listOf("Ethical collection", "Mounting pins", "Labeling data")).toString(),
                habitat = "Science labs, natural history",
                dangerLevel = "Info",
                dangerDescription = "Tutorial on ethical and professional bug collection.",
                timestamp = System.currentTimeMillis()
            )
            20011L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_green_beetle}",
                commonName = "The John Landy Butterfly Collection",
                scientificName = "Remarkable Collection",
                confidence = 100,
                description = "A world-renowned collection showcasing thousands of rare and colorful butterflies from all continents, demonstrating the incredible diversity of Lepidoptera.",
                characteristicsJson = JSONArray(listOf("Butterfly diversity", "Global specimens", "Historical collection")).toString(),
                habitat = "Museums, botanical archives",
                dangerLevel = "Info",
                dangerDescription = "Overview of one of the world's most famous collections.",
                timestamp = System.currentTimeMillis()
            )
            20012L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_onboarding_atlas_moth}",
                commonName = "The Scott Sisters Collection",
                scientificName = "Remarkable Collection",
                confidence = 100,
                description = "Historical biological illustrations and collections from the 19th century in Australia, detailing the life cycle of butterflies and moths with scientific precision.",
                characteristicsJson = JSONArray(listOf("Scientific illustrations", "19th century history", "Moths life cycles")).toString(),
                habitat = "Art galleries, natural archives",
                dangerLevel = "Info",
                dangerDescription = "Historical tribute to early women of science and art.",
                timestamp = System.currentTimeMillis()
            )
            else -> throw IllegalArgumentException("Unknown static ID: $id")
        }
    }

    suspend fun insertInsect(insect: InsectEntity): Long {
        return insectDao.insertInsect(insect)
    }

    suspend fun deleteInsectById(id: Long) {
        insectDao.deleteInsectById(id)
    }

    suspend fun identifyInsect(bitmap: Bitmap): InsectInfo? {
        return geminiServiceClient.identifyInsect(bitmap)
    }
}
