package jhelp;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TableSaver {

    private String termSaveScript;
    private String definitionSaveScript;
    private DataSource dataSource;

    public void setTermSaveScript(String termSaveScript) {
        this.termSaveScript = termSaveScript;
    }

    public void setDefinitionSaveScript(String definitionSaveScript) {
        this.definitionSaveScript = definitionSaveScript;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveTables(){
        try {
            dataSource.getConnection().createStatement().execute(termSaveScript);
            dataSource.getConnection().createStatement().execute(definitionSaveScript);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
