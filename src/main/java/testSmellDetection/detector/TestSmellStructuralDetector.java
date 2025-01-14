package testSmellDetection.detector;

import com.intellij.openapi.project.Project;
import testSmellDetection.bean.PsiClassBean;
import testSmellDetection.bean.PsiMethodBean;
import testSmellDetection.structuralRules.EagerTestStructural;
import testSmellDetection.structuralRules.GeneralFixtureStructural;
import testSmellDetection.structuralRules.LackOfCohesionOfTestSmellStructural;
import testSmellDetection.testSmellInfo.eagerTest.EagerTestInfo;
import testSmellDetection.testSmellInfo.eagerTest.MethodWithEagerTest;
import testSmellDetection.testSmellInfo.generalFixture.GeneralFixtureInfo;
import testSmellDetection.testSmellInfo.generalFixture.MethodWithGeneralFixture;
import testSmellDetection.testSmellInfo.hardCodedTestData.HardCodedTestDataInfo;
import testSmellDetection.testSmellInfo.hardCodedTestData.MethodWithHardCodedTestData;
import testSmellDetection.testSmellInfo.lackOfCohesion.LackOfCohesionInfo;
import testSmellDetection.testSmellInfo.testCodeDuplication.MethodWithTestCodeDuplication;
import testSmellDetection.testSmellInfo.testCodeDuplication.TestCodeDuplicationInfo;
import testSmellDetection.testSmellInfo.mysteryGuest.MethodWithMysteryGuest;
import testSmellDetection.testSmellInfo.mysteryGuest.MysteryGuestInfo;
import testSmellDetection.textualRules.EagerTestTextual;
import testSmellDetection.textualRules.GeneralFixtureTextual;
import testSmellDetection.textualRules.HardCodedTestDataTextual;
import testSmellDetection.textualRules.LackOfCohesionOfTestSmellTextual;
import testSmellDetection.textualRules.TestCodeDuplicationTextual;
import testSmellDetection.textualRules.MysteryGuestTextual;
import utility.ConverterUtilities;
import utility.TestSmellUtilities;

import java.util.ArrayList;

public class TestSmellStructuralDetector implements IDetector{
    private ArrayList<PsiClassBean> classBeans;
    private ArrayList<PsiClassBean> testClasses;
    private ArrayList<PsiClassBean> productionClasses;
    private Project project;

    //variabili per l'analisi di GeneralFixture
    private int numberOfProductionTypes = 3;
    private int numberOfObjectUsesInSetup = 3;

    public TestSmellStructuralDetector(Project project){
        classBeans = ConverterUtilities.getClassesFromPackages(project);
        testClasses = TestSmellUtilities.getAllTestClasses(classBeans);
        productionClasses = TestSmellUtilities.getAllProductionClasses(classBeans, testClasses);
        this.project = project;
    }

    public ArrayList<GeneralFixtureInfo> executeDetectionForGeneralFixture() {
        ArrayList<GeneralFixtureInfo> classesWithGeneralFixture = new ArrayList<>();
        for(PsiClassBean testClass : testClasses){
            if(GeneralFixtureStructural.isGeneralFixture(testClass, productionClasses, testClasses, numberOfProductionTypes, numberOfObjectUsesInSetup)){
                ArrayList<MethodWithGeneralFixture> methodWithGeneralFixtures = GeneralFixtureTextual.checkMethodsThatCauseGeneralFixture(testClass);
                classesWithGeneralFixture.add(new GeneralFixtureInfo(testClass, methodWithGeneralFixtures));
            }
        }
        return classesWithGeneralFixture;
    }


    public ArrayList<EagerTestInfo> executeDetectionForEagerTest() {
        ArrayList<EagerTestInfo> classesWithEagerTest = new ArrayList<>();
        for(PsiClassBean testClass : testClasses){
            if(testClass.getProductionClass() != null) {
                if (EagerTestStructural.isEagerTest(testClass, testClasses, productionClasses)) {
                    ArrayList<MethodWithEagerTest> methodWithEagerTests = EagerTestTextual.checkMethodsThatCauseEagerTest(testClass, testClass.getProductionClass());
                    classesWithEagerTest.add(new EagerTestInfo(testClass, testClass.getProductionClass(), methodWithEagerTests));
                }
            }
        }
        return classesWithEagerTest;
    }


    public ArrayList<LackOfCohesionInfo> executeDetectionForLackOfCohesion() {
        ArrayList<LackOfCohesionInfo> classesWithLackOfCohesion = new ArrayList<>();
        for(PsiClassBean testClass : testClasses){
            if(testClass.getProductionClass() != null) {
                if (LackOfCohesionOfTestSmellStructural.isLackOfCohesion(testClass)) {
                    ArrayList<PsiMethodBean> methodsWithLackOfCohesion = LackOfCohesionOfTestSmellTextual.checkMethodsThatCauseLackOfCohesion(testClass);
                    classesWithLackOfCohesion.add(new LackOfCohesionInfo(testClass, testClass.getProductionClass(), methodsWithLackOfCohesion));
                }
            }
        }
        return classesWithLackOfCohesion;
    }
  
    public ArrayList<HardCodedTestDataInfo> executeDetectionForHardCodedTestData() {
        ArrayList<HardCodedTestDataInfo> classesWithHardCodedTestData = new ArrayList<>();
        for(PsiClassBean testClass : testClasses){
            if(testClass.getProductionClass() != null) {
                ArrayList<MethodWithHardCodedTestData> methodsWithHardCodedTestData = HardCodedTestDataTextual.checkMethodsThatCauseHardCodedTestData(testClass);
                if (methodsWithHardCodedTestData != null) {
                    classesWithHardCodedTestData.add(new HardCodedTestDataInfo(testClass, methodsWithHardCodedTestData));
                }
            }
        }
        return classesWithHardCodedTestData;
    }

    public ArrayList<MysteryGuestInfo> executeDetectionForMysteryGuest() {
        ArrayList<MysteryGuestInfo> classesWithMysteryGuest = new ArrayList<>();
        for(PsiClassBean testClass : testClasses){
            if(testClass.getProductionClass() != null) {
                ArrayList<MethodWithMysteryGuest> methodWithMysteryGuests = MysteryGuestTextual.checkMethodsThatCauseMysteryGuest(testClass);
                if (methodWithMysteryGuests != null) {
                    classesWithMysteryGuest.add(new MysteryGuestInfo(testClass, methodWithMysteryGuests));
                }
            }
        }
        return classesWithMysteryGuest;
    }
    
    public ArrayList<TestCodeDuplicationInfo> executeDetectionForTestCodeDuplication() {
        ArrayList<TestCodeDuplicationInfo> classesWithTestCodeDuplication = new ArrayList<>();
        for (PsiClassBean testClass : testClasses) {
            try {
                if (testClass.getProductionClass() != null) {
                    ArrayList<MethodWithTestCodeDuplication> methodsWithTestCodeDuplication = TestCodeDuplicationTextual.checkMethodsThatCauseTestCodeDuplication(testClass, project);
                    if (methodsWithTestCodeDuplication != null) {
                        classesWithTestCodeDuplication.add(new TestCodeDuplicationInfo(testClass, methodsWithTestCodeDuplication));
                    }
                }
            } catch (Exception e) {}
        }
        return classesWithTestCodeDuplication;
    }
}
