package com.fitai.service;

import com.fitai.entity.*;
import com.fitai.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializerService {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final NutritionGoalRepository nutritionGoalRepository;
    private final ExerciseRepository exerciseRepository;
    private final FoodItemRepository foodItemRepository;
    private final KnowledgeDocumentRepository knowledgeRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutSessionRepository sessionRepository;
    private final MealEntryRepository mealEntryRepository;
    private final FitnessGoalRepository fitnessGoalRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializerService(UserRepository userRepository,
                                  UserProfileRepository profileRepository,
                                  NutritionGoalRepository nutritionGoalRepository,
                                  ExerciseRepository exerciseRepository,
                                  FoodItemRepository foodItemRepository,
                                  KnowledgeDocumentRepository knowledgeRepository,
                                  WorkoutPlanRepository workoutPlanRepository,
                                  WorkoutSessionRepository sessionRepository,
                                  MealEntryRepository mealEntryRepository,
                                  FitnessGoalRepository fitnessGoalRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.nutritionGoalRepository = nutritionGoalRepository;
        this.exerciseRepository = exerciseRepository;
        this.foodItemRepository = foodItemRepository;
        this.knowledgeRepository = knowledgeRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.sessionRepository = sessionRepository;
        this.mealEntryRepository = mealEntryRepository;
        this.fitnessGoalRepository = fitnessGoalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        logger.info("Initializing FitAI seed data...");

        // 1. Seed Admin User
        User admin = userRepository.findByEmail("admin@fitai.com").orElseGet(() -> {
            User u = new User("admin@fitai.com", passwordEncoder.encode("Admin@123"), "System Administrator", Role.ROLE_ADMIN);
            return userRepository.save(u);
        });

        // 2. Seed Demo User
        User demoUser = userRepository.findByEmail("demo@fitai.com").orElseGet(() -> {
            User u = new User("demo@fitai.com", passwordEncoder.encode("Demo@123"), "Alex Morgan", Role.ROLE_USER);
            User saved = userRepository.save(u);

            UserProfile profile = new UserProfile();
            profile.setUser(saved);
            profile.setAge(26);
            profile.setGender("FEMALE");
            profile.setHeightCm(168.0);
            profile.setWeightKg(62.5);
            profile.setTargetWeightKg(60.0);
            profile.setActivityLevel("MODERATELY_ACTIVE");
            profile.setExperienceLevel("INTERMEDIATE");
            profile.setPreferredWorkoutDurationMinutes(50);
            profile.setPrimaryGoal("STRENGTH");
            profile.setDietaryPreferences("High Protein, Mediterranean");
            profile.setHealthNotes("Mild left knee tightness during cold weather.");
            profileRepository.save(profile);

            NutritionGoal goal = new NutritionGoal();
            goal.setUser(saved);
            goal.setDailyCaloriesTarget(2100);
            goal.setDailyProteinTarget(135);
            goal.setDailyCarbsTarget(230);
            goal.setDailyFatTarget(60);
            goal.setWaterIntakeMlTarget(2800);
            nutritionGoalRepository.save(goal);

            return saved;
        });

        // 3. Seed Exercises Library
        if (exerciseRepository.count() == 0) {
            seedExercises();
        }

        // 4. Seed Food Items Library
        if (foodItemRepository.count() == 0) {
            seedFoodItems();
        }

        // 5. Seed Knowledge Base Documents for RAG
        if (knowledgeRepository.count() == 0) {
            seedKnowledgeDocuments();
        }

        // 6. Seed Workout Templates and Demo User Data
        if (workoutPlanRepository.count() == 0) {
            seedWorkoutPlans(demoUser);
        }

        if (sessionRepository.count() == 0) {
            seedDemoHistory(demoUser);
        }

        logger.info("FitAI seed data initialization complete.");
    }

    private void seedExercises() {
        List<Exercise> exercises = Arrays.asList(
            new Exercise("Barbell Back Squat", "Fundamental lower body compound exercise for quad, glute, and spinal erector strength.", "LEGS", "BARBELL", "INTERMEDIATE", "STRENGTH", "1. Place bar on upper traps. 2. Feet shoulder-width. 3. Descend until hips are below knees. 4. Drive back up through midfoot.", "Keep chest proud, brace core, and avoid inward knee cave."),
            new Exercise("Flat Barbell Bench Press", "Classic upper body compound push exercise developing chest, anterior delts, and triceps.", "CHEST", "BARBELL", "INTERMEDIATE", "STRENGTH", "1. Lie flat with eyes under bar. 2. Grip slightly wider than shoulder-width. 3. Lower bar to mid-sternum. 4. Press upward to lockout.", "Maintain slight arch in lower back and keep feet planted firmly."),
            new Exercise("Conventional Deadlift", "Posterior chain builder targeting hamstrings, glutes, lats, and erector spinae.", "BACK", "BARBELL", "ADVANCED", "STRENGTH", "1. Stand with bar over mid-foot. 2. Hinge at hips to grip bar. 3. Pull chest up, engage lats. 4. Drive floor away through heels.", "Never round your lumbar spine under heavy loads."),
            new Exercise("Overhead Shoulder Press", "Vertical pressing movement building shoulder boulders, upper chest, and core stability.", "SHOULDERS", "BARBELL", "INTERMEDIATE", "STRENGTH", "1. Clean or unrack bar to collarbone. 2. Press straight up overhead. 3. Lock out overhead with head slightly forward.", "Avoid excessive lower back hyperextension."),
            new Exercise("Pull-Ups", "Bodyweight vertical pull for wide lats, biceps, and grip strength.", "BACK", "BODYWEIGHT", "INTERMEDIATE", "STRENGTH", "1. Pronated grip wider than shoulders. 2. Pull elbows down toward ribs until chin clears bar. 3. Lower under control.", "Do not swing or kip excessively."),
            new Exercise("Push-Ups (Standard / Incline)", "Classic bodyweight horizontal pressing exercise for chest, triceps, and anterior delts.", "CHEST", "BODYWEIGHT", "BEGINNER", "STRENGTH", "1. Hands slightly wider than shoulders. 2. Keep body in rigid plank. 3. Lower chest to floor. 4. Push back up.", "For beginners, elevate hands on a bench or sturdy counter."),
            new Exercise("Incline Dumbbell Press", "Targeting clavicular upper chest fibers and anterior deltoids.", "CHEST", "DUMBBELL", "BEGINNER", "STRENGTH", "1. Set bench to 30-45 degrees. 2. Press dumbbells from shoulder height to peak contraction.", "Control eccentric phase for 2-3 seconds."),
            new Exercise("Romanian Deadlift (RDL)", "Hip-hinge staple emphasizing hamstring and glute hypertrophy.", "LEGS", "DUMBBELL", "BEGINNER", "STRENGTH", "1. Hold dumbbells at thighs. 2. Push hips back while keeping soft knees. 3. Lower to shin level and squeeze glutes up.", "Keep weights close to legs throughout movement."),
            new Exercise("Dumbbell Goblet Squat", "Accessible knee-friendly squat variation that reinforces upright torso mechanics.", "LEGS", "DUMBBELL", "BEGINNER", "STRENGTH", "1. Hold single dumbbell at chest. 2. Stand feet shoulder-width. 3. Squat deep keeping elbows inside knees.", "Great home-friendly alternative to barbell squats."),
            new Exercise("Dumbbell Bicep Curls", "Isolation movement targeting both heads of the biceps brachii.", "ARMS", "DUMBBELL", "BEGINNER", "STRENGTH", "1. Hold dumbbells at sides. 2. Supinate wrists while flexing elbows. 3. Lower slowly.", "Keep elbows tucked to side; avoid swinging torso."),
            new Exercise("Tricep Skull Crushers", "Direct triceps builder isolating all three triceps heads.", "ARMS", "BARBELL", "INTERMEDIATE", "STRENGTH", "1. Lie on bench with EZ bar overhead. 2. Bend elbows to lower bar towards forehead. 3. Extend elbows.", "Keep upper arms perpendicular to floor."),
            new Exercise("Resistance Band Rows", "Home-friendly back builder developing rhomboids and mid-traps without heavy equipment.", "BACK", "RESISTANCE_BAND", "BEGINNER", "STRENGTH", "1. Anchor band around feet or door. 2. Pull handles towards ribcage. 3. Squeeze shoulder blades together.", "Keep shoulders relaxed and do not shrug up."),
            new Exercise("Bodyweight Glute Bridge", "Low-impact posterior chain activation exercise safeguarding lower back health.", "LEGS", "BODYWEIGHT", "BEGINNER", "STRENGTH", "1. Lie flat on back with knees bent at 90 degrees. 2. Drive through heels to raise hips. 3. Squeeze glutes at top.", "Avoid overarching lumbar spine at the peak."),
            new Exercise("Hanging Leg Raise", "Dynamic core movement for rectus abdominis and hip flexor strength.", "CORE", "BODYWEIGHT", "INTERMEDIATE", "STRENGTH", "1. Hang from bar with active shoulders. 2. Raise straight legs to 90 degrees. 3. Lower without swinging.", "Engage abs before initiating swing."),
            new Exercise("Plank Hold", "Isometric anti-extension core stability exercise.", "CORE", "BODYWEIGHT", "BEGINNER", "STRENGTH", "1. Forearms on floor, body in straight line. 2. Contract glutes, quads, and abs.", "Do not allow hips to sag or hike upward."),
            new Exercise("Mountain Climbers", "Dynamic high-cadence core and cardiovascular conditioning movement.", "CORE", "BODYWEIGHT", "BEGINNER", "CARDIO", "1. Start in high plank. 2. Alternate driving knees toward chest at brisk tempo.", "Maintain flat back without bounding hips up and down."),
            new Exercise("Bulgarian Split Squat", "Unilateral leg builder fixing muscle imbalances in quads and glutes.", "LEGS", "DUMBBELL", "INTERMEDIATE", "STRENGTH", "1. Rear foot elevated on bench. 2. Descend front knee until parallel. 3. Press through front heel.", "Keep torso upright or slightly hinged forward."),
            new Exercise("Cable Lat Pulldown", "Vertical pulling alternative to pull-ups for back width.", "BACK", "CABLE", "BEGINNER", "STRENGTH", "1. Wide grip on bar. 2. Pull down to upper chest while squeezing shoulder blades together.", "Avoid leaning back past 15 degrees."),
            new Exercise("Lateral Shoulder Raises", "Medial delt builder providing shoulder width and V-taper look.", "SHOULDERS", "DUMBBELL", "BEGINNER", "STRENGTH", "1. Slight forward lean with dumbbells. 2. Raise arms out to sides up to shoulder height.", "Lead with elbows and avoid shrugging traps."),
            new Exercise("HIIT Jump Rope Intervals", "High-efficiency cardiovascular conditioning improving footwork and calf endurance.", "FULL_BODY", "BODYWEIGHT", "BEGINNER", "HIIT", "1. 40 seconds continuous jumping. 2. 20 seconds rest. Repeat for 10 rounds.", "Land lightly on balls of feet with knees soft.")
        );
        exerciseRepository.saveAll(exercises);
    }

    private void seedFoodItems() {
        List<FoodItem> foods = Arrays.asList(
            new FoodItem("Skinless Chicken Breast", 100.0, "g", 165.0, 31.0, 0.0, 3.6, "PROTEIN"),
            new FoodItem("Atlantic Salmon Fillet", 100.0, "g", 208.0, 20.0, 0.0, 13.0, "PROTEIN"),
            new FoodItem("Whole Large Egg", 1.0, "piece", 72.0, 6.3, 0.4, 4.8, "PROTEIN"),
            new FoodItem("Egg Whites", 100.0, "g", 52.0, 11.0, 0.7, 0.2, "PROTEIN"),
            new FoodItem("Non-fat Greek Yogurt", 100.0, "g", 59.0, 10.0, 3.6, 0.4, "DAIRY"),
            new FoodItem("Whey Protein Isolate", 30.0, "g", 120.0, 25.0, 2.0, 1.0, "PROTEIN"),
            new FoodItem("Rolled Oats", 50.0, "g", 190.0, 6.5, 33.0, 3.0, "CARBS"),
            new FoodItem("Brown Jasmine Rice (Cooked)", 150.0, "g", 168.0, 3.8, 35.0, 1.4, "CARBS"),
            new FoodItem("Sweet Potato (Baked)", 150.0, "g", 135.0, 3.0, 31.0, 0.2, "CARBS"),
            new FoodItem("Quinoa (Cooked)", 150.0, "g", 180.0, 6.0, 32.0, 2.8, "CARBS"),
            new FoodItem("Whole Grain Sourdough Bread", 50.0, "g", 120.0, 4.0, 24.0, 1.0, "CARBS"),
            new FoodItem("Fresh Hass Avocado", 100.0, "g", 160.0, 2.0, 8.5, 14.7, "HEALTHY_FATS"),
            new FoodItem("Raw Almonds", 30.0, "g", 175.0, 6.0, 6.0, 15.0, "HEALTHY_FATS"),
            new FoodItem("Extra Virgin Olive Oil", 15.0, "ml", 120.0, 0.0, 0.0, 14.0, "HEALTHY_FATS"),
            new FoodItem("Steamed Broccoli Florets", 100.0, "g", 35.0, 2.8, 7.0, 0.4, "VEGETABLES"),
            new FoodItem("Baby Spinach", 100.0, "g", 23.0, 2.9, 3.6, 0.4, "VEGETABLES"),
            new FoodItem("Fresh Blueberries", 100.0, "g", 57.0, 0.7, 14.0, 0.3, "FRUITS"),
            new FoodItem("Medium Banana", 1.0, "piece", 105.0, 1.3, 27.0, 0.3, "FRUITS"),
            new FoodItem("Natural Peanut Butter", 32.0, "g", 190.0, 8.0, 7.0, 16.0, "HEALTHY_FATS"),
            new FoodItem("Almond Milk (Unsweetened)", 240.0, "ml", 30.0, 1.0, 1.0, 2.5, "BEVERAGES"),
            // Indian & Condition-Friendly Foods
            new FoodItem("Yellow Moong Dal Tadka", 150.0, "g", 155.0, 9.2, 23.5, 3.2, "INDIAN"),
            new FoodItem("Grilled Paneer Tikka", 100.0, "g", 260.0, 18.2, 4.8, 19.5, "INDIAN"),
            new FoodItem("Moong Dal & Brown Rice Khichdi", 200.0, "g", 220.0, 8.5, 38.0, 4.0, "INDIAN"),
            new FoodItem("Chana Masala (Chickpeas)", 150.0, "g", 215.0, 9.0, 34.0, 5.2, "INDIAN"),
            new FoodItem("Ragi Roti (Millet Flatbread)", 60.0, "piece", 145.0, 3.6, 29.0, 1.4, "INDIAN"),
            new FoodItem("Palak Paneer (Spinach Cottage Cheese)", 150.0, "g", 230.0, 12.5, 7.5, 17.0, "INDIAN"),
            new FoodItem("Sprouted Moong Bean Salad", 100.0, "g", 110.0, 7.8, 19.0, 0.8, "INDIAN"),
            new FoodItem("Fresh Curd / Dahi (Probiotic)", 100.0, "g", 65.0, 4.2, 5.0, 3.3, "INDIAN"),
            new FoodItem("Roasted Chana (Bengal Gram)", 50.0, "g", 180.0, 10.5, 28.0, 2.8, "INDIAN"),
            new FoodItem("Tofu Stir-Fry with Greens", 150.0, "g", 175.0, 16.0, 6.0, 10.0, "VEGAN")
        );
        foodItemRepository.saveAll(foods);
    }

    private void seedKnowledgeDocuments() {
        List<KnowledgeDocument> docs = Arrays.asList(
            new KnowledgeDocument(
                "Progressive Overload Principle",
                "EXERCISE_SCIENCE",
                "Progressive overload involves systematically increasing the demands placed on the musculoskeletal system to stimulate continuous muscular hypertrophy and neuromuscular strength gains. Methods include adding load, performing additional repetitions, improving movement velocity, increasing training volume, or decreasing inter-set rest intervals. For optimal hypertrophy, aim for 10-20 weekly sets per muscle group near muscular failure (1-3 RIR).",
                "hypertrophy, progressive_overload, strength, training_volume",
                "progressive overload hypertrophy muscle growth strength repetitions sets volume load rir",
                "National Strength and Conditioning Association (NSCA)"
            ),
            new KnowledgeDocument(
                "Macronutrient Distribution for Athletes",
                "NUTRITION",
                "Optimal athletic body composition requires target daily macronutrient balances. Protein intake should be 1.6 to 2.2 grams per kilogram of bodyweight distributed evenly across 3 to 5 meals to stimulate muscle protein synthesis (MPS). Dietary carbohydrates (3 to 7 g/kg depending on activity level) replenish glycogen stores. Fats should comprise 20-35% of total caloric intake to support endocrine balance and fat-soluble vitamin absorption.",
                "protein, carbohydrates, macros, nutrition, mps",
                "macronutrients protein carbohydrates fats calories nutrition diet macro distribution grams",
                "International Society of Sports Nutrition (ISSN)"
            ),
            new KnowledgeDocument(
                "PCOS Nutritional Management & Insulin Sensitivity",
                "NUTRITION",
                "Polycystic Ovary Syndrome (PCOS) is frequently driven by underlying hyperinsulinemia and insulin resistance. Nutritional strategies emphasize low glycemic index (GI) carbohydrates to blunt postprandial insulin surges, adequate protein (25-30g per meal) to stabilize blood glucose, anti-inflammatory omega-3 fatty acids, and myo-inositol rich foods (citrus fruits, beans, nuts). Strength training enhances peripheral GLUT4 translocation, greatly improving cellular insulin sensitivity without relying exclusively on insulin secretion. (Disclaimer: Educational information; consult your physician.)",
                "pcos, insulin_resistance, hormonal_health, womens_health, low_gi",
                "pcos polycystic ovary insulin resistance low gi glucose carbohydrates hormonal women menstrual",
                "American College of Obstetricians and Gynecologists (ACOG)"
            ),
            new KnowledgeDocument(
                "Glycemic Index & Carbohydrate Quality in Diabetes Management",
                "NUTRITION",
                "For individuals managing Type 2 Diabetes or prediabetes, carbohydrate quality is paramount over extreme carbohydrate restriction. Complex unrefined carbs rich in viscous soluble fiber (such as whole millets, oats, legumes, lentils) slow gastric emptying and attenuate blood glucose spikes. Pairing carbohydrates with dietary proteins and healthy monounsaturated fats significantly dampens glycemic response. Consistent aerobic exercise and resistance training enhance non-insulin-mediated glucose uptake into skeletal muscles.",
                "diabetes, glycemic_index, blood_sugar, insulin, carbohydrates",
                "diabetes glycemic index blood sugar glucose insulin carbs fiber lentils millets HbA1c",
                "American Diabetes Association (ADA)"
            ),
            new KnowledgeDocument(
                "Protein Optimization in Indian and Plant-Based Diets",
                "NUTRITION",
                "Traditional Indian vegetarian meals often lean heavy on carbohydrates with incomplete amino acid profiles. To achieve the leucine threshold (~2.5-3.0g) necessary for triggering Muscle Protein Synthesis (MPS), combine diverse protein sources such as lentils (dal) paired with cereals (rice/roti) for complementary essential amino acid profiles (lysine and methionine). High-density vegetarian protein anchors include low-fat paneer, Greek yogurt/curd, roasted chana, edamame, and tofu.",
                "indian_diet, vegetarian, plant_protein, amino_acids, mps",
                "indian diet vegetarian protein dal paneer curd chana lentils amino acids leucine",
                "Indian Council of Medical Research (ICMR-NIN)"
            ),
            new KnowledgeDocument(
                "Sleep Architecture and Athletic Recovery",
                "RECOVERY",
                "Deep Slow-Wave Sleep (Stage 3 NREM) is the primary physiological window during which human growth hormone (hGH) is secreted, promoting tissue repair, cellular restoration, and glycogen replenishment. Athletes averaging fewer than 7 hours of sleep exhibit 1.7x higher injury susceptibility, impaired carbohydrate metabolism, and diminished cognitive reaction times. Prioritize 7 to 9 hours of dark, cool (18-20°C) sleep nightly.",
                "sleep, recovery, growth_hormone, circadian_rhythm",
                "sleep recovery rest fatigue injury hormones growth hormone slow wave",
                "American College of Sports Medicine (ACSM)"
            ),
            new KnowledgeDocument(
                "Creatine Monohydrate Supplementation",
                "SUPPLEMENTATION",
                "Creatine monohydrate is the most extensively researched ergonomic aid in sports nutrition. It increases intramuscular phosphocreatine stores, accelerating ATP synthesis during high-intensity, short-duration bouts (sprints, heavy lifting). Recommended dosing is 3-5 grams daily taken consistently without cycling. It produces 5-15% increases in maximal strength and power without adverse renal side effects in healthy individuals.",
                "creatine, supplements, atp, strength",
                "creatine monohydrate atp power strength supplements water retention dosing",
                "ISSN Position Stand on Creatine"
            ),
            new KnowledgeDocument(
                "Deload Strategies and Overreaching Prevention",
                "RECOVERY",
                "A deload is a planned temporary reduction in training stress designed to dissipate accumulated systemic fatigue while preserving neuromuscular adaptations. Typical deloads decrease training volume by 40-50% while maintaining moderate intensity (loads) for one week every 6-8 weeks of intensive training. Indications for a deload include chronic joint soreness, sleep disruption, elevated resting heart rate, and plateauing strength.",
                "deload, fatigue, overtraining, fatigue_management",
                "deload overtraining fatigue recovery joint soreness volume reduction burnout",
                "Sports Medicine Journal"
            ),
            new KnowledgeDocument(
                "Hydration and Electrolyte Homeostasis",
                "NUTRITION",
                "Even mild dehydration (loss of 2% body mass from water) degrades aerobic endurance, elevates core body temperature, and impairs mental focus. Sweat consists of water and crucial electrolytes including sodium, potassium, and magnesium. Athletes should consume 500ml water 2 hours pre-workout, 150-250ml every 15-20 minutes during vigorous exercise, and replace 125-150% of weight lost post-session.",
                "hydration, electrolytes, water, sodium",
                "hydration water electrolytes sweat sodium fluid intake endurance cramps",
                "Gatorade Sports Science Institute (GSSI)"
            ),
            new KnowledgeDocument(
                "Dynamic Warm-ups vs Static Stretching",
                "INJURY_PREVENTION",
                "Static stretching prior to heavy explosive lifting can temporarily diminish maximal power and tendon stiffness. In contrast, dynamic warm-ups (leg swings, arm circles, hip openers, bodyweight lunges) elevate core temperature, lubricate synovial joints, and prime motor unit recruitment without reducing force output. Reserve static stretching for post-workout cool-downs to lengthen chronically tight muscle groups.",
                "warmup, stretching, mobility, injury_prevention",
                "warmup stretching dynamic static mobility joints flexibility injury",
                "British Journal of Sports Medicine"
            ),
            new KnowledgeDocument(
                "Energy Balance: Deficit vs Surplus Principles",
                "NUTRITION",
                "Body mass change is fundamentally governed by the thermodynamic relationship between energy intake and expenditure. For safe fat loss, a moderate caloric deficit of 300-500 kcal below Total Daily Energy Expenditure (TDEE) targets ~0.5 to 1.0% bodyweight loss per week while sparing lean muscle mass. For muscle gain, a lean surplus of 200-350 kcal supports hypertrophy while minimizing excess adiposity.",
                "tdee, calories, fat_loss, surplus, energy_balance",
                "calories energy deficit surplus tdee fat loss weight loss muscle building",
                "Mayo Clinic Sports Medicine"
            )
        );
        knowledgeRepository.saveAll(docs);
    }

    private void seedWorkoutPlans(User demoUser) {
        List<Exercise> allExercises = exerciseRepository.findAll();
        if (allExercises.isEmpty()) return;

        WorkoutPlan plan1 = new WorkoutPlan();
        plan1.setName("Full Body Functional Strength");
        plan1.setDescription("A balanced 3-day full body foundation routine emphasizing compound lifts.");
        plan1.setDifficulty("INTERMEDIATE");
        plan1.setDaysPerWeek(3);
        plan1.setTemplate(true);
        plan1.setUser(demoUser);

        int order = 0;
        for (Exercise ex : allExercises.subList(0, Math.min(5, allExercises.size()))) {
            WorkoutPlanExercise pe = new WorkoutPlanExercise(plan1, ex, 3, 10, 90, order++);
            plan1.addPlanExercise(pe);
        }
        workoutPlanRepository.save(plan1);

        WorkoutPlan plan2 = new WorkoutPlan();
        plan2.setName("Upper Body Hypertrophy Focus");
        plan2.setDescription("Targeted upper body split developing chest, back, shoulders, and arms.");
        plan2.setDifficulty("ADVANCED");
        plan2.setDaysPerWeek(4);
        plan2.setTemplate(true);
        plan2.setUser(null); // System template
        workoutPlanRepository.save(plan2);

        WorkoutPlan plan3 = new WorkoutPlan();
        plan3.setName("Home Minimalist Strength & Core");
        plan3.setDescription("No-gym home routine using bodyweight, resistance bands, and dumbbells. Perfect for busy schedules.");
        plan3.setDifficulty("BEGINNER");
        plan3.setDaysPerWeek(3);
        plan3.setTemplate(true);
        plan3.setUser(null);
        workoutPlanRepository.save(plan3);
    }

    private void seedDemoHistory(User demoUser) {
        List<Exercise> exercises = exerciseRepository.findAll();
        List<FoodItem> foods = foodItemRepository.findAll();
        if (exercises.isEmpty() || foods.isEmpty()) return;

        LocalDate today = LocalDate.now();

        // Seed 4 days of workouts for Demo User
        for (int i = 0; i < 4; i++) {
            LocalDate date = today.minusDays(i * 2);
            WorkoutSession session = new WorkoutSession();
            session.setUser(demoUser);
            session.setSessionName(i % 2 == 0 ? "Full Body Strength Session" : "Upper Body Blast");
            session.setSessionDate(date);
            session.setDurationMinutes(50);
            session.setCaloriesBurned(360);
            session.setStatus("COMPLETED");
            session.setNotes("Felt energetic, hit all prescribed target reps.");

            for (int e = 0; e < 3; e++) {
                Exercise ex = exercises.get(e);
                for (int set = 1; set <= 3; set++) {
                    WorkoutExerciseLog log = new WorkoutExerciseLog(session, ex, set, 10, 45.0 + (e * 15), true);
                    session.addExerciseLog(log);
                }
            }
            sessionRepository.save(session);
        }

        // Seed 3 days of meal entries for Demo User
        for (int i = 0; i < 3; i++) {
            LocalDate date = today.minusDays(i);

            MealEntry breakfast = new MealEntry();
            breakfast.setUser(demoUser);
            breakfast.setFoodItem(foods.get(6)); // Rolled Oats
            breakfast.setMealType("BREAKFAST");
            breakfast.setEntryDate(date);
            breakfast.setServings(1.5);
            breakfast.recalculateNutrition();
            mealEntryRepository.save(breakfast);

            MealEntry lunch = new MealEntry();
            lunch.setUser(demoUser);
            lunch.setFoodItem(foods.get(0)); // Chicken Breast
            lunch.setMealType("LUNCH");
            lunch.setEntryDate(date);
            lunch.setServings(2.0);
            lunch.recalculateNutrition();
            mealEntryRepository.save(lunch);

            MealEntry dinner = new MealEntry();
            dinner.setUser(demoUser);
            dinner.setFoodItem(foods.get(1)); // Salmon
            dinner.setMealType("DINNER");
            dinner.setEntryDate(date);
            dinner.setServings(1.5);
            dinner.recalculateNutrition();
            mealEntryRepository.save(dinner);
        }

        // Seed Fitness Goal
        FitnessGoal goal = new FitnessGoal();
        goal.setUser(demoUser);
        goal.setGoalType("STRENGTH");
        goal.setTitle("Squat 80 kg for 5 reps");
        goal.setDescription("Working towards linear progressive overload on Back Squats.");
        goal.setTargetValue(80.0);
        goal.setCurrentValue(65.0);
        goal.setMetricUnit("kg");
        goal.setTargetDate(today.plusMonths(2));
        goal.setCompleted(false);
        fitnessGoalRepository.save(goal);
    }
}
