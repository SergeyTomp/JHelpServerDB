package jhelp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@PropertySource("classpath:app.properties")
public class TableSaver {

    @Value("${script.exportTable.term.begin}" + "${term.source.path}" + "${script.exportTable.end}")
    private String termSaveScript;

    @Value("${script.exportTable.definition.begin}" + "${definition.source.path}" + "${script.exportTable.end}")
    private String definitionSaveScript;

    @Value("${term.source.path}")
    private String termSourceFile;

    @Value("${definition.source.path}")
    private String definitionSourceFile;

    @Value("${term.reserve.file.name}")
    private String termReserveCopyNameFile;

    @Value("${definition.reserve.file.name}")
    private String definitionReserveCopyNameFile;

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveTables(){
        try {
            Path termSourcePath = Paths.get(termSourceFile);
            Path definitionSourcePath = Paths.get(definitionSourceFile);
            Files.move(termSourcePath, termSourcePath.resolveSibling(termReserveCopyNameFile), REPLACE_EXISTING);
            Files.move(definitionSourcePath, definitionSourcePath.resolveSibling(definitionReserveCopyNameFile), REPLACE_EXISTING);
            dataSource.getConnection().createStatement().execute(termSaveScript);
            dataSource.getConnection().createStatement().execute(definitionSaveScript);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
