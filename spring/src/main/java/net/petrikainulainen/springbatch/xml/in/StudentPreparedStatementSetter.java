package net.petrikainulainen.springbatch.xml.in;

import net.petrikainulainen.springbatch.student.StudentDTO;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class is used to set the actual parameter values of a
 * prepared statement.
 */
final class StudentPreparedStatementSetter implements ItemPreparedStatementSetter<StudentDTO> {

    @Override
    public void setValues(StudentDTO student, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, student.getEmailAddress());
        preparedStatement.setString(2, student.getName());
        preparedStatement.setString(3, student.getPurchasedPackage());
    }
}
