package service;

import model.Project;
import repository.implementation.ProjectRepositoryImpl;
import repository.interfaces.ProjectRepository;

import java.util.List;
import java.util.Optional;

public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService() {
        this.projectRepository = new ProjectRepositoryImpl();
    }

    public Optional<Project> get(long id){
        return projectRepository.get(id);
    }

    public List<Project> getAllProjects() {
        return projectRepository.getAllProjects();
    }



    public void updateProject(Project project) {
        projectRepository.updateProject(project);
    }


    public void deleteProject(int id) {
        projectRepository.deleteProject(id);
    }

    public void addProject(Project project) {
      projectRepository.addProject(project);
    }

    public List<Project> searchProjects(String name) {
        return projectRepository.searchProjectsByName(name);
    }

    public List<Project> getAllProjectsPaginated(int page, int itemsPerPage) {
        int offset = (page - 1) * itemsPerPage;
        return projectRepository.getAllProjectsPaginated(itemsPerPage, offset);
    }


    public int getTotalProjectCount() {
        return projectRepository.getTotalProjectCount();
    }


    public List<Object[]> getProjectSummaries(int page, int pageSize) {
        return projectRepository.getProjectSummaries(page, pageSize);
    }

}
