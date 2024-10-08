package repository.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.DatabaseConnection;
import model.Member;
import model.Project;
import model.Task;
import model.enums.Role;
import model.enums.TaskPriority;
import model.enums.TaskStatus;
import repository.interfaces.TaskRepository;

public class TaskRepositoryImpl implements TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepositoryImpl.class);

    private static final String SQL_FIND_BY_ID = "SELECT * FROM task WHERE id = ? ";
    private static final String SQL_LIST = "SELECT task_with_members.id_task, task_with_members.title AS title, task_with_members.description, task_with_members.priority, task_with_members.task_statut, task_with_members.project_id, member.id AS member_id, member.first_name, member.last_name, member.email, member.role, task_with_members.assign_date FROM ( SELECT task.id AS id_task, task.*, member_task.* FROM task LEFT JOIN member_task ON task.id = member_task.task_id AND member_task.assign_date = ( SELECT MAX(assign_date) FROM member_task mt WHERE mt.task_id = task.id ) ) AS task_with_members LEFT JOIN member ON task_with_members.member_id = member.id WHERE task_with_members.project_id = ?";
    private static final String SQL_INSERT = "INSERT INTO task (`title`, `description`, `priority`, `task_statut`, `project_id`) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE task SET title = ?, description = ?, priority = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM task WHERE id = ?";
    private static final String SQL_UPDATE_STATUS = "UPDATE task SET task_statut = ? WHERE id = ?";
    private static final String SQL_ASSIGN_TASK = "INSERT INTO member_task (`task_id`, `member_id`, `assign_date`) VALUES (?, ?, ?)";
    private static final String SQL_GET_MEMBER_TASKS = "SELECT task.id AS id_task, task.title, task.description, task.priority, task.task_statut, task.project_id, member.id AS member_id, member.first_name, member.last_name, member.email, member.role, member_task.assign_date FROM task JOIN member_task ON task.id = member_task.task_id JOIN member ON member_task.member_id = member.id WHERE member.id = ?";

    @Override
    public List<Task> getAllTasks(Project project) {
        List<Task> tasks = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_LIST);) {
            ps.setLong(1, project.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getLong("id_task"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setTaskPriority(TaskPriority.valueOf(rs.getString("priority")));
                    task.setTaskStatus(TaskStatus.valueOf(rs.getString("task_statut")));

                    // Handle possible null values for assign_date
                    Timestamp assignDate = rs.getTimestamp("assign_date");
                    if (assignDate != null) {
                        task.setAssignDate(assignDate.toLocalDateTime());
                    }

                    Long memberId = rs.getLong("member_id");
                    if (!rs.wasNull()) {
                        Member member = new Member();
                        member.setId(memberId);
                        member.setFirstName(rs.getString("first_name"));
                        member.setLastName(rs.getString("last_name"));
                        member.setEmail(rs.getString("email"));
                        member.setRole(Role.valueOf(rs.getString("role")));

                        task.setMember(member);
                    }

                    task.setProject(project);
                    tasks.add(task);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public Optional<Task> get(long id) {
        Project project = new Project();
        Task task = null;
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID);) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    TaskPriority taskPriority = TaskPriority.valueOf(rs.getString("priority"));
                    TaskStatus taskStatus = TaskStatus.valueOf(rs.getString("task_statut"));

                    task = new Task(id, title, description, taskPriority, taskStatus, project);
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.ofNullable(task);

    }

    @Override
    public void assignMemberToTask(long task_id, long member_id) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_ASSIGN_TASK)) {

            ps.setLong(1, task_id);
            ps.setLong(2, member_id);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            logger.info("Assigned task of id : " + task_id + " to member : " + member_id);
        } catch (SQLException e) {
            logger.error("Error saving task: " + e.getMessage(), e);
        }
    }

    @Override
    public long save(Task task) {
        long id;
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getTaskPriority().toString());
            ps.setString(4, task.getTaskStatus().toString());
            ps.setLong(5, task.getProject().getId());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                        logger.info("Task with id " + id + " added successfully");
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving task: " + e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public void save(Task task, long member_id) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getTaskPriority().toString());
            ps.setString(4, task.getTaskStatus().toString());
            ps.setLong(5, task.getProject().getId());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        assignMemberToTask(rs.getLong(1), member_id);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error saving task: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(long id, Task updatedTask) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, updatedTask.getTitle());
            ps.setString(2, updatedTask.getDescription());
            ps.setString(3, updatedTask.getTaskPriority().toString());
            ps.setLong(4, id);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Task with ID " + id + " was successfully updated.");
            } else {
                logger.warn("No task found with ID " + id);
            }

        } catch (SQLException e) {
            logger.error("Error updating task: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateTaskStatus(long id, TaskStatus taskStatus) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_UPDATE_STATUS)) {

            ps.setString(1, taskStatus.toString());
            ps.setLong(2, id);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Task status with ID " + id + " was successfully updated.");
            } else {
                logger.warn("No task found with ID " + id);
            }

        } catch (SQLException e) {
            logger.error("Error updating task: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting task: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getTaskByMemberId(Long memberId) {
        List<Task> tasks = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(SQL_GET_MEMBER_TASKS)) {
            stmt.setLong(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getLong("id_task"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setTaskPriority(TaskPriority.valueOf(rs.getString("priority")));
                    task.setTaskStatus(TaskStatus.valueOf(rs.getString("task_statut")));

                    Member member = new Member();
                    member.setId(rs.getLong("member_id"));
                    member.setFirstName(rs.getString("first_name"));
                    member.setLastName(rs.getString("last_name"));
                    member.setEmail(rs.getString("email"));
                    member.setRole(Role.valueOf(rs.getString("role")));

                    // Set assign date if available
                    Timestamp assignDate = rs.getTimestamp("assign_date");
                    if (assignDate != null) {
                        task.setAssignDate(assignDate.toLocalDateTime());
                    }

                    // Link the member and the task
                    task.setMember(member);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving tasks for member: " + e.getMessage(), e);
        }

        return tasks;
    }

}
