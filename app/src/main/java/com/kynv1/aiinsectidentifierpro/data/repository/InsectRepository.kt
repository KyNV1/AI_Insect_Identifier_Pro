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
                imageResId = R.drawable.img_basic_ash_black_slug,
                category = "Most Common"
            ),
            InsectShort(
                id = 10002L,
                commonName = "Black Oil Beetle",
                scientificName = "Meloe proscarabaeus",
                imageResId = R.drawable.img_basic_black_oil_beetle,
                category = "Most Common"
            ),
            InsectShort(
                id = 10003L,
                commonName = "Broom-tip Moth",
                scientificName = "Chesias rufata",
                imageResId = R.drawable.img_basic_broom_tip_moth,
                category = "Most Common"
            ),
            InsectShort(
                id = 10004L,
                commonName = "Buffish Mining Bee",
                scientificName = "Andrena fulva",
                imageResId = R.drawable.img_basic_buffish_mining_bee,
                category = "Most Common"
            ),
            InsectShort(
                id = 10005L,
                commonName = "Common Wasp",
                scientificName = "Vespula vulgaris",
                imageResId = R.drawable.img_basic_common_wasp,
                category = "Most Common"
            ),

            // Garden Insect
            InsectShort(
                id = 10006L,
                commonName = "Brown-lipped Snail",
                scientificName = "Cepaea nemoralis",
                imageResId = R.drawable.img_basic_brown_lipped_snail,
                category = "Garden Insect"
            ),
            InsectShort(
                id = 10007L,
                commonName = "Black and red froghopper",
                scientificName = "Cercopis vulnerata",
                imageResId = R.drawable.img_basic_black_red_froghopper,
                category = "Garden Insect"
            ),
            InsectShort(
                id = 10008L,
                commonName = "The Sabre Wasp",
                scientificName = "Rhyssa persuasoria",
                imageResId = R.drawable.img_basic_sabre_wasp,
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
                imageResId = R.drawable.img_basic_red_ladybug,
                category = "Garden Insect"
            )
        )
    }

    fun getMockArticles(): List<HomeArticle> {
        return listOf(
            // Fun Bug Facts
            HomeArticle(
                id = 20001L,
                commonName = "Deer Tick",
                scientificName = "Ixodes scapularis",
                imageResId = R.drawable.img_article_tick,
                category = "Fun Bug Facts"
            ),
            HomeArticle(
                id = 20002L,
                commonName = "Eastern Subterranean Termite",
                scientificName = "Reticulitermes flavipes",
                imageResId = R.drawable.img_article_termite,
                category = "Fun Bug Facts"
            ),
            HomeArticle(
                id = 20003L,
                commonName = "Pill Bug",
                scientificName = "Armadillidium vulgare",
                imageResId = R.drawable.img_article_pill_bug,
                category = "Fun Bug Facts"
            ),

            // Pest Control
            HomeArticle(
                id = 20004L,
                commonName = "German Cockroach",
                scientificName = "Blattella germanica",
                imageResId = R.drawable.img_article_kitchen_pest,
                category = "Pest Control"
            ),
            HomeArticle(
                id = 20005L,
                commonName = "Boxelder Bug",
                scientificName = "Boisea trivittata",
                imageResId = R.drawable.img_article_boxelder_bug,
                category = "Pest Control"
            ),
            HomeArticle(
                id = 20006L,
                commonName = "House Mouse",
                scientificName = "Mus musculus",
                imageResId = R.drawable.img_article_mouse,
                category = "Pest Control"
            ),

            // Bug Bite Help
            HomeArticle(
                id = 20007L,
                commonName = "Black Widow Spider",
                scientificName = "Latrodectus mactans",
                imageResId = R.drawable.img_article_spider_bite,
                category = "Bug Bite Help"
            ),
            HomeArticle(
                id = 20008L,
                commonName = "Common Wasp",
                scientificName = "Vespula vulgaris",
                imageResId = R.drawable.img_article_wasp_sting,
                category = "Bug Bite Help"
            ),
            HomeArticle(
                id = 20009L,
                commonName = "Cat Flea",
                scientificName = "Ctenocephalides felis",
                imageResId = R.drawable.img_article_flea,
                category = "Bug Bite Help"
            ),

            // Remarkable Collection
            HomeArticle(
                id = 20010L,
                commonName = "Stag Beetle",
                scientificName = "Lucanus cervus",
                imageResId = R.drawable.img_article_insect_collection,
                category = "Remarkable Collection"
            ),
            HomeArticle(
                id = 20011L,
                commonName = "Monarch Butterfly",
                scientificName = "Danaus plexippus",
                imageResId = R.drawable.img_article_butterfly_collection,
                category = "Remarkable Collection"
            ),
            HomeArticle(
                id = 20012L,
                commonName = "Atlas Moth",
                scientificName = "Attacus atlas",
                imageResId = R.drawable.img_article_insect_collection,
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
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_ash_black_slug}",
                commonName = "Ash-black Slug",
                scientificName = "Limax cinereoniger",
                confidence = 97,
                description = "The ash-black slug (Limax cinereoniger) is the largest species of land slug in the world, with mature specimens often reaching lengths of up to 20 cm or more. Characterized by its dark, ash-grey to black coloration with a pale keel running down its back, this massive gastropod is native to European woodland ecosystems.\n\nUnlike many common garden slugs, the ash-black slug is primarily a forest dweller. It feeds on wild fungi, lichen, moss, and decaying plant matter, which makes it an essential species for nutrient cycling and forest decomposition processes.\n\nTo survive dry conditions, these slugs secrete a thick protective slime coat and remain hidden under rotting logs or leaf litter during the daytime, coming out to forage only in cool, humid night conditions.",
                characteristicsJson = JSONArray(listOf("Largest land slug", "Air-breathing", "Feeds on fungi and algae")).toString(),
                habitat = "Old coniferous and deciduous forests",
                dangerLevel = "Low",
                dangerDescription = "Completely harmless to humans, plays an important role as a decomposer.",
                timestamp = System.currentTimeMillis()
            )
            10002L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_black_oil_beetle}",
                commonName = "Black Oil Beetle",
                scientificName = "Meloe proscarabaeus",
                confidence = 94,
                description = "The black oil beetle (Meloe proscarabaeus) is a unique, flightless beetle species native to Europe. It has a distinctive bulbous, soft abdomen that is much larger than its wings, giving it an oil-like, glossy black sheen. When threatened, they exhibit a defense mechanism known as reflexive bleeding, secreting oily droplets of hemolymph from their leg joints containing cantharidin.\n\nTheir life cycle is highly complex and parasitic. Female beetles lay thousands of eggs in the soil. The newly hatched triungulin larvae climb wildflowers and cling to visiting solitary ground bees. Once inside the bee's nest, the triungulin consumes the bee's eggs and stored honey, transforming into a grub-like larva before pupating.\n\nBecause cantharidin is a potent toxin that causes painful blisters on human skin, it is strongly advised to observe these beetles without handling them.",
                characteristicsJson = JSONArray(listOf("Secretes oily hemolymph", "Parasitic larvae", "Flightless beetle")).toString(),
                habitat = "Grassy areas, woodlands, and flower fields",
                dangerLevel = "Medium",
                dangerDescription = "Can cause skin irritation or blistering if handled roughly, due to the cantharidin toxin they secrete.",
                timestamp = System.currentTimeMillis()
            )
            10003L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_broom_tip_moth}",
                commonName = "Broom-tip Moth",
                scientificName = "Chesias rufata",
                confidence = 96,
                description = "The broom-tip moth (Chesias rufata) is a small nocturnal moth belonging to the family Geometridae. Found throughout Europe and North Africa, adults are characterized by their beautifully camouflaged grey-brown wings with intricate vertical line markings that mimic tree bark and plant stems.\n\nThe larvae (caterpillars) feed almost exclusively on the leaves and tender green stems of broom shrubs (Cytisus scoparius) and other related plants, making them highly dependent on healthy heathland and sand dune habitats.\n\nAdults are active from late spring to mid-summer, flying primarily at night and hiding perfectly flat against wooden fences or branches during the daytime to escape birds and other predators.",
                characteristicsJson = JSONArray(listOf("Night-flying", "Camouflaged wings", "Feeds on broom shrubs")).toString(),
                habitat = "Heathlands, sandy hills, and dry valleys",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans and pets.",
                timestamp = System.currentTimeMillis()
            )
            10004L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_buffish_mining_bee}",
                commonName = "Buffish Mining Bee",
                scientificName = "Andrena fulva",
                confidence = 95,
                description = "The tawny mining bee (Andrena fulva), also known as the buffish mining bee, is a common and distinctive European solitary bee. Females are easily recognized by their dense, bright reddish-orange coat on their thorax and abdomen, which mimics the appearance of tiny bumblebees.\n\nAs solitary bees, they do not live in hives. Instead, fertilized females dig individual nesting burrows directly into lawns, pathways, and loose garden soil, leaving characteristic small mounds of soil resembling miniature volcanoes. They provision each underground cell with a ball of pollen and nectar for their developing larvae.\n\nThese bees are completely non-aggressive and lack the colony-defense reflex of social wasps or honey bees, making them excellent, safe pollinators for backyard gardens and orchards.",
                characteristicsJson = JSONArray(listOf("Solitary nester", "Tawny orange coat", "Excellent pollinator")).toString(),
                habitat = "Gardens, parks, lawns, and meadows",
                dangerLevel = "Low",
                dangerDescription = "Non-aggressive and rarely stings. If stung, symptoms are minor unless allergic.",
                timestamp = System.currentTimeMillis()
            )
            10005L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_common_wasp}",
                commonName = "Common Wasp",
                scientificName = "Vespula vulgaris",
                confidence = 98,
                description = "The common wasp (Vespula vulgaris) is a social insect species widespread across the temperate regions of the Northern Hemisphere. They live in highly structured annual colonies consisting of a queen, male drones, and sterile female workers. Their paper-like nests are constructed from chewed wood fibers mixed with saliva, typically hidden underground or in hollow cavities.\n\nDuring the spring and summer, wasps are active predators. They hunt caterpillars, flies, and other garden pests to feed protein to their developing larvae, serving as a natural form of pest control. In late summer, when the queen stops laying eggs and workers no longer receive sweet secretions from the larvae, they actively seek alternative sugary food sources, leading to conflicts with humans.\n\nUnlike honey bees, wasps can sting repeatedly. Their stings are painful and contain venom that can cause severe, life-threatening allergic reactions in sensitive individuals.",
                characteristicsJson = JSONArray(listOf("Social insect", "Paper-like nests", "Predatory on pests")).toString(),
                habitat = "Woodlands, gardens, orchards, and urban areas",
                dangerLevel = "High",
                dangerDescription = "Aggressive when defending nests. Stings are painful and can cause severe allergic reactions (anaphylaxis) in sensitive individuals.",
                timestamp = System.currentTimeMillis()
            )
            10006L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_brown_lipped_snail}",
                commonName = "Brown-lipped Snail",
                scientificName = "Cepaea nemoralis",
                confidence = 93,
                description = "The brown-lipped snail (Cepaea nemoralis), also known as the grove snail, is one of the most common and widely distributed species of land snails in Europe and North America. It is famous for its extreme polymorphism, meaning its shells come in a vast range of colors (yellow, pink, brown) and bands (ranging from zero to five dark spiral bands).\n\nThe snail's name comes from the dark brown lip at the rim of its shell opening in mature adults. They feed primarily on decaying plant matter, algae, and lichens, helping recycle organic material back into the soil, though they may occasionally nibble on young garden seedlings.\n\nTheir varied shell colors are a classic study in evolutionary biology, as their visual appearance protects them from thrushes and other predators depending on the background habitat they inhabit.",
                characteristicsJson = JSONArray(listOf("Variable shell color", "Dark brown lip rim", "Herbivorous diet")).toString(),
                habitat = "Gardens, sand dunes, and hedgerows",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans, but can be a minor pest to young garden plants.",
                timestamp = System.currentTimeMillis()
            )
            10007L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_black_red_froghopper}",
                commonName = "Black and red froghopper",
                scientificName = "Cercopis vulnerata",
                confidence = 95,
                description = "The red-and-black froghopper (Cercopis vulnerata) is a small, striking insect native to Europe. Easily recognized by its bold black body decorated with three bright red warning spots, it utilizes this contrast (aposematism) to warn birds and other predators that it is foul-tasting.\n\nAs nymphs, froghoppers live inside a protective mass of white, frothy spit-like bubbles, often referred to as 'cuckoo spit,' which they secrete from their alimentary canal. This foam insulates their soft bodies from extreme temperatures and keeps them moist while hiding them from predators.\n\nAdults feed on plant sap using their specialized piercing-sucking mouthparts. They are famous for their powerful hind legs, which allow them to jump immense heights when disturbed.",
                characteristicsJson = JSONArray(listOf("Bold warning colors", "Nymphs make spit-like nests", "Herbivorous sap sucker")).toString(),
                habitat = "Grassy meadows, forest edges, and gardens",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans and animals.",
                timestamp = System.currentTimeMillis()
            )
            10008L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_sabre_wasp}",
                commonName = "The Sabre Wasp",
                scientificName = "Rhyssa persuasoria",
                confidence = 96,
                description = "The sabre wasp (Rhyssa persuasoria) is one of the largest and most impressive species of ichneumonid wasps in Europe. The females possess an exceptionally long, needle-like ovipositor (which resembles a sabre or stinger) that can be longer than their actual body.\n\nDespite its menacing appearance, this structure is not a stinger and cannot harm humans. Instead, the female wasp uses it to drill deep into solid tree trunks to deposit eggs directly onto the larvae of wood-boring horntail wasps or beetles hidden inside.\n\nThey have an incredibly sensitive sense of smell, allowing them to detect the vibrations and scent of host larvae through inches of solid wood, making them crucial regulators of forest timber pests.",
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
                description = "The western honey bee (Apis mellifera) is a highly social insect species globally valued for its agricultural importance. They live in large, permanent colonies comprising a single reproductive queen, thousands of sterile female workers, and male drones. They are famous for their ability to construct wax combs and produce sweet honey from flower nectar.\n\nHoney bees are among the most critical pollinators on Earth, responsible for fertilizing one-third of the food crops consumed by humans. They communicate the location of rich flower patches to hive-mates using an extraordinary 'waggle dance' that conveys distance and direction relative to the sun.\n\nWhile workers possess a venomous stinger to defend the hive, they are generally gentle and will only sting as a last resort, as the stinger's barbs pull out of their bodies, causing them to die shortly after stinging.",
                characteristicsJson = JSONArray(listOf("Social insect", "Produces honey", "Important pollinator")).toString(),
                habitat = "Gardens, woodlands, orchards, and meadows",
                dangerLevel = "Low",
                dangerDescription = "Honey bees are generally non-aggressive and will only sting if they feel threatened or to defend their hive. Stings can cause allergic reactions in sensitive individuals.",
                timestamp = System.currentTimeMillis()
            )
            10010L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_basic_red_ladybug}",
                commonName = "Red Ladybug",
                scientificName = "Harmonia axyridis",
                confidence = 95,
                description = "The Asian ladybeetle (Harmonia axyridis), commonly known as the red ladybug or harlequin ladybird, is a highly variable beetle species native to eastern Asia. It is famous for its extreme color variations, ranging from pale yellow to bright red, and containing anywhere from zero to over twenty black spots.\n\nThese ladybugs are voracious predators of soft-bodied garden pests like aphids and scale insects, making them highly beneficial for natural agriculture. However, in late autumn, they can become a household nuisance as they gather in massive numbers on sunny exterior walls and find pathways inside homes to hibernate.\n\nWhen threatened, they can bite slightly and release a smelly yellow fluid (reflex bleeding) that can stain fabrics and cause allergic reactions in some people.",
                characteristicsJson = JSONArray(listOf("Voracious predator of aphids", "Variable coloration", "Can aggregate in large numbers")).toString(),
                habitat = "Agricultural fields, gardens, and forests",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans, though they can bite occasionally and produce a smelly yellow fluid when threatened.",
                timestamp = System.currentTimeMillis()
            )
            // Articles Mapping
            20001L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_tick}",
                commonName = "Deer Tick",
                scientificName = "Ixodes scapularis",
                confidence = 100,
                description = "The deer tick (Ixodes scapularis), also known as the black-legged tick, is a hard-bodied tick native to North America. Ticks are small, parasitic arachnids that feed on the blood of birds and mammals, including humans, to survive and reproduce.\n\nTicks do not jump or fly. Instead, they climb to the tips of grasses and shrubs and wait with their front legs extended—a behavior called 'questing'—until a suitable host brushes past, allowing them to latch on.\n\nDeer ticks are highly notorious because they are the primary vector for Borrelia burgdorferi, the bacterium that causes Lyme disease in humans and pets. Their bites are usually painless, so checking for ticks after outdoor activities in wooded areas is highly recommended.",
                characteristicsJson = JSONArray(listOf("Blood-feeding parasite", "Eight-legged arachnid", "Vector of Lyme disease")).toString(),
                habitat = "Deciduous forests, tall grasses, brushy areas, and leaf litter",
                dangerLevel = "High",
                dangerDescription = "Highly dangerous vector for Lyme disease, Anaplasmosis, and Babesiosis through its bites.",
                timestamp = System.currentTimeMillis()
            )
            20002L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_termite}",
                commonName = "Eastern Subterranean Termite",
                scientificName = "Reticulitermes flavipes",
                confidence = 100,
                description = "The Eastern subterranean termite is the most common and widely distributed termite in North America. They are highly social, wood-destroying insects that feed on cellulose and build extensive underground tunnels.\n\nSubterranean termites live in massive colonies underground, comprising a king, queen, soldiers, and workers. Because they require moisture to survive, they construct protective mud tubes (mud shelter tunnels) along foundation walls to travel between the soil and wood sources above ground.\n\nWhile they play a vital ecological role in breaking down dead trees in forests, they are extremely destructive to homes, causing billions of dollars in structural damage annually by chewing through structural timber, drywall, and wooden framing.",
                characteristicsJson = JSONArray(listOf("Social colony insects", "Feeds on cellulose/wood", "Builds mud shelter tubes")).toString(),
                habitat = "Soil, damp wood, tree stumps, and house foundations",
                dangerLevel = "Medium",
                dangerDescription = "Harmless to humans physically, but causes severe and costly structural damage to wood.",
                timestamp = System.currentTimeMillis()
            )
            20003L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_pill_bug}",
                commonName = "Pill Bug",
                scientificName = "Armadillidium vulgare",
                confidence = 100,
                description = "Also known as roly-polies, pill bugs are terrestrial isopods (a type of crustacean, not actual insects). They are famous for their ability to roll into a tight ball when threatened. They are decomposers that eat organic leaf litter.\n\nPill bugs require a moist environment to survive because they breathe through gill-like structures on their undersides. They are primarily nocturnal and feed on decaying leaves, wood, and organic garden debris, playing an essential role in recycling nutrients back into the soil.\n\nBecause they do not bite, sting, or carry diseases, they are completely harmless to humans and are frequently studied in classrooms as a friendly introduction to ecology.",
                characteristicsJson = JSONArray(listOf("Terrestrial crustacean", "Rolls into a ball", "Ecosystem decomposer")).toString(),
                habitat = "Moist organic soils, under stones, rotting wood, and leaf piles",
                dangerLevel = "Low",
                dangerDescription = "Completely harmless to humans and pets, beneficial for soil health.",
                timestamp = System.currentTimeMillis()
            )
            20004L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_kitchen_pest}",
                commonName = "German Cockroach",
                scientificName = "Blattella germanica",
                confidence = 100,
                description = "The German cockroach is a small, highly resilient species of cockroach. It is a major global household pest that reproduces rapidly and is closely associated with human habitats, especially food storage areas.\n\nThey have a flat, light-brown body with two dark parallel stripes running down their pronotum. Being nocturnal, they hide in dark, warm, and humid crevices during the day, coming out at night to search for food scraps, grease, and moisture.\n\nGerman cockroaches are a serious health hazard. They carry bacteria like Salmonella on their bodies, contaminate food, and release proteins in their droppings and outer skins that can trigger severe allergies or asthma attacks in children.",
                characteristicsJson = JSONArray(listOf("Flat, light-brown body", "Nocturnal activity", "Fast running ability")).toString(),
                habitat = "Kitchens, bathrooms, pantries, and warm crevices",
                dangerLevel = "Medium",
                dangerDescription = "Can contaminate food with bacteria and trigger allergies or asthma through droppings.",
                timestamp = System.currentTimeMillis()
            )
            20005L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_boxelder_bug}",
                commonName = "Boxelder Bug",
                scientificName = "Boisea trivittata",
                confidence = 100,
                description = "Boxelder bugs are true bugs native to North America, easily identified by their black bodies with bright orange-red markings. They feed on seeds of boxelder, maple, and ash trees, and congregate on sunny walls in autumn.\n\nThey feed by using their specialized piercing-sucking mouthparts to puncture seeds and soft plant tissue, drawing out vital nutrients. While they can feed on boxelder and maple seeds, they rarely cause significant damage to the trees themselves.\n\nIn autumn, they look for warm crevices to hibernate, often entering homes through cracks around windows and doors. While they do not bite or carry diseases, their sheer numbers can be a nuisance, and they release a foul odor when crushed.",
                characteristicsJson = JSONArray(listOf("Black with red accents", "Piercing-sucking mouth", "Aggregates for warmth")).toString(),
                habitat = "Boxelder and maple trees, sunny building walls, and indoor spaces",
                dangerLevel = "Low",
                dangerDescription = "Harmless to humans, though their droppings can stain fabrics and they emit an odor when crushed.",
                timestamp = System.currentTimeMillis()
            )
            20006L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_mouse}",
                commonName = "House Mouse",
                scientificName = "Mus musculus",
                confidence = 100,
                description = "The house mouse is a small rodent characterized by a pointed snout, large rounded ears, and a long scaly tail. It is a highly adaptable mammal that lives in close association with humans, feeding on grains and food scraps.\n\nHouse mice are nocturnal and possess an incredible sense of smell, hearing, and touch. They are highly reproductive, with a single female capable of producing up to 10 litters of 5-6 pups every year, allowing infestations to grow rapidly.\n\nInside buildings, they chew through drywall, cardboard, and electrical wiring to build nests, which presents a significant fire hazard. They also contaminate pantry items with their droppings and urine, potentially spreading pathogens.",
                characteristicsJson = JSONArray(listOf("Pointed snout, scaly tail", "Nocturnal rodent", "Highly reproductive")).toString(),
                habitat = "Homes, garages, pantries, barns, and grassy fields",
                dangerLevel = "Medium",
                dangerDescription = "Can chew electric wires (fire hazard), destroy drywall, and carry diseases like Salmonella.",
                timestamp = System.currentTimeMillis()
            )
            20007L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_spider_bite}",
                commonName = "Black Widow Spider",
                scientificName = "Latrodectus mactans",
                confidence = 100,
                description = "The black widow is a highly venomous spider. The female is easily recognized by its shiny black body and a distinctive red hourglass marking on the underside of its abdomen, producing extremely strong webs.\n\nBlack widows construct irregular, tangled webs made of exceptionally strong silk to catch insects. They are shy, solitary creatures that prefer dark, undisturbed locations like woodpiles, cluttered garages, and crawl spaces.\n\nWhile their bites are rare and only occur if they are pressed or threatened, their neurotoxic venom is extremely potent. A bite causes severe localized pain, muscle cramps, and nausea, and requires immediate medical observation.",
                characteristicsJson = JSONArray(listOf("Shiny black body", "Red hourglass shape", "Strong, irregular web")).toString(),
                habitat = "Dark, sheltered, undisturbed spaces like woodpiles, garages, and crawl spaces",
                dangerLevel = "High",
                dangerDescription = "Neurotoxic venom from bites causes severe pain, muscle cramps, and requires prompt medical care.",
                timestamp = System.currentTimeMillis()
            )
            20008L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_wasp_sting}",
                commonName = "Common Wasp",
                scientificName = "Vespula vulgaris",
                confidence = 100,
                description = "The common wasp is a social wasp species found across the Northern Hemisphere. They live in large colonies with nests made of chewed wood pulp, and they are active predators of garden pest larvae.\n\nWorkers construct large paper-like nests using chewed wood pulp mixed with their saliva, often hidden in tree cavities, wall voids, or underground tunnels. Wasps play an important ecological role as predators of agricultural pests like caterpillars and flies.\n\nHowever, they can be highly aggressive when defending their nests. Unlike honey bees, wasps can sting repeatedly without dying, and their stings are painful and can trigger severe allergic reactions (anaphylaxis) in sensitive individuals.",
                characteristicsJson = JSONArray(listOf("Yellow and black stripes", "Social nest builders", "Repeated stinging capacity")).toString(),
                habitat = "Woodlands, gardens, wall voids, and underground cavities",
                dangerLevel = "High",
                dangerDescription = "Highly aggressive when defending nests, stings are painful and can trigger severe anaphylactic shock.",
                timestamp = System.currentTimeMillis()
            )
            20009L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_flea}",
                commonName = "Cat Flea",
                scientificName = "Ctenocephalides felis",
                confidence = 100,
                description = "The cat flea is a small, wingless, flattened parasite of cats, dogs, and humans. It is famous for its incredible jumping ability and feeds exclusively on the blood of its hosts to survive and lay eggs.\n\nThey have a laterally flattened body covered in backwards-pointing spines, which allows them to move swiftly through animal fur. They can jump up to 30 cm vertically to find a host.\n\nFlea bites usually appear in clusters of small red bumps around ankles and legs, causing intense itching. Flea infestations on pets can lead to skin irritation, hair loss, and potentially transmit tapeworms or other diseases.",
                characteristicsJson = JSONArray(listOf("Wingless blood-feeder", "Lateral flat body", "Exceptional jumper")).toString(),
                habitat = "Pet fur, upholstery, carpets, and animal bedding",
                dangerLevel = "Medium",
                dangerDescription = "Bites cause intense itching, skin irritation, dermatitis, and can transmit tapeworms.",
                timestamp = System.currentTimeMillis()
            )
            20010L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_insect_collection}",
                commonName = "Stag Beetle",
                scientificName = "Lucanus cervus",
                confidence = 100,
                description = "The stag beetle is one of the largest beetles in Europe. Males are famous for their giant antler-like mandibles, which they use to wrestle rivals during the mating season on tree trunks.\n\nThey spend the majority of their lives (up to 3-7 years) underground as large C-shaped grubs, feeding on decaying wood from old oak tree stumps. The adults emerge for only a few weeks in summer to mate and lay eggs.\n\nDespite their fearsome appearance, stag beetles are completely harmless to humans. The male's large mandibles are relatively weak and cannot cause serious pinches, while the female's smaller mandibles are stronger but are only used for chewing wood.",
                characteristicsJson = JSONArray(listOf("Antler-like mandibles", "Large dark chitin body", "Slow flight in dusk")).toString(),
                habitat = "Decaying oak forests, parks, and tree hollows",
                dangerLevel = "Low",
                dangerDescription = "Completely harmless. The large mandibles are weak and cannot pinch humans hard.",
                timestamp = System.currentTimeMillis()
            )
            20011L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_butterfly_collection}",
                commonName = "Monarch Butterfly",
                scientificName = "Danaus plexippus",
                confidence = 100,
                description = "The monarch butterfly is a milkweed butterfly famous for its orange-black warning coloration and its spectacular long-distance annual migration across North America. Its caterpillars feed solely on milkweed.\n\nThe bright orange and black wing patterns serve as warning coloration to birds and other predators, indicating that the butterfly is highly toxic because its caterpillars feed on milkweed plants containing cardiac glycosides.\n\nEach autumn, millions of monarch butterflies travel thousands of miles from Canada and the United States to wintering sites in central Mexico, displaying one of the most incredible migration journeys in the natural world.",
                characteristicsJson = JSONArray(listOf("Orange-black wings", "Long-distance migration", "Milkweed diet toxicity")).toString(),
                habitat = "Fields, meadows, gardens, and milkweed patches",
                dangerLevel = "Low",
                dangerDescription = "Completely harmless to humans, only toxic to birds and predators if ingested.",
                timestamp = System.currentTimeMillis()
            )
            20012L -> InsectEntity(
                id = id,
                imageUri = "android.resource://$packageName/${R.drawable.img_article_insect_collection}",
                commonName = "Atlas Moth",
                scientificName = "Attacus atlas",
                confidence = 100,
                description = "The Atlas moth is one of the largest lepidopterans in the world, with a wingspan reaching up to 25–30 cm. Native to the forests of Southeast Asia, adults have no mouthparts and live only for a few days to mate.\n\nTheir large wings are a beautiful reddish-brown color with triangular transparent windows and a wing tip pattern that closely resembles a cobra head to scare off avian predators. They spend their short adult life of 1-2 weeks solely searching for a mate.\n\nThey are completely harmless and gentle creatures. In Taiwan, their large paper-like cocoons are sometimes repurposed as small wallets or pocket cases.",
                characteristicsJson = JSONArray(listOf("Giant wingspan", "Cobra head wing tip pattern", "Short adult life")).toString(),
                habitat = "Tropical and subtropical dry forests of Southeast Asia",
                dangerLevel = "Low",
                dangerDescription = "Completely harmless and gentle giant moth.",
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

    suspend fun getChatResponse(prompt: String): String {
        return geminiServiceClient.getChatResponse(prompt)
    }
}
