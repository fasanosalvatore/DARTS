package main.contextualAnalysis;

import main.testSmellDetection.testSmellInfo.TestSmellInfo;
import org.repodriller.RepoDriller;
import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.GitRepository;

import java.util.Arrays;

//class able to manage the data extraction
public class DataMiner implements Study{

    private TestSmellInfo smell;
    private String productionClass;
    private String projectPath;

    public DataMiner(TestSmellInfo info, String projectPath){
        smell = info;
        productionClass = info.getClassWithSmell().getProductionClass().getName();
        this.projectPath = projectPath;
    }

    @Override
    public void execute() {
        //implements extraction's logic
        new RepositoryMining()
                .in(GitRepository.singleProject(projectPath))
                .through(Commits.all())
                .process(new DevelopersVisitor(productionClass), new CSVFile("C:\\Users\\gsuli\\Desktop\\devs.csv"))
                .mine();
    }
}
