package controller;

import model.Squad;
import model.enums.ProjectStatus;
import service.ProjectService;
import model.Project;
import service.SquadService;
import util.Validator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectServlet extends HttpServlet {

    private ProjectService projectService = new ProjectService();
    private Validator validator = new Validator();



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int currentPage = 1;
        int itemsPerPage = 6;

        String searchQuery = request.getParameter("search");
        System.out.println("Search Query: " + searchQuery);
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            List<Project> projects = projectService.searchProjects(searchQuery);

            if (projects.isEmpty()) {
                request.setAttribute("message", "Project does not exist!");
                System.out.println("Aucun projet trouvé pour la requête: " + searchQuery);
            } else {
                request.setAttribute("message", "Search results for: " + searchQuery);
                projects.forEach(project -> System.out.println(project.getName()));
            }

            request.setAttribute("projects", projects);
            request.getRequestDispatcher("views/projects.jsp").forward(request, response);
            return;
        }



        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            currentPage = Integer.parseInt(pageParam);
        }

        int totalProjects = projectService.getTotalProjectCount();
        int totalPages = (int) Math.ceil((double) totalProjects / itemsPerPage);

        List<Project> projects = projectService.getAllProjectsPaginated(currentPage, itemsPerPage);

        SquadService squadService = new SquadService();
        List<Squad> squads = squadService.getAllSquads();
        request.setAttribute("projects", projects);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("squads", squads);
        for (Squad squad : squads) {
            System.out.println("Squad ID: " + squad.getId() + ", Name: " + squad.getName());
        }


        request.getRequestDispatcher("views/projects.jsp").forward(request, response);
    }




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");
        List<String> errors = new ArrayList<>();

        if (action != null && action.equals("update")) {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            LocalDate dateDebut = LocalDate.parse(request.getParameter("dateDebut"));
            LocalDate dateFin = LocalDate.parse(request.getParameter("dateFin"));
            String statut = request.getParameter("statut");
            String squadId = request.getParameter("squadId");

            Project project = new Project();
            project.setId(id);
            project.setName(nom);
            project.setDescription(description);
            project.setStartDate(dateDebut);
            project.setEndDate(dateFin);
            project.setStatus(ProjectStatus.valueOf(statut));


            Squad squad = new Squad();
            squad.setId(Long.parseLong(squadId));
            project.setSquad(squad);

            errors = validator.validateProject(project);

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("project", project);
                List<Project> projects = projectService.getAllProjects();
                request.setAttribute("projects", projects);
                request.getRequestDispatcher("views/projects.jsp").forward(request, response);
                return;
            }

            projectService.updateProject(project);
            request.setAttribute("message", "Projet mis à jour avec succès !");
        } else if (action != null && action.equals("delete")) {
            try {
                int id = Integer.parseInt(idParam);
                projectService.deleteProject(id);
                request.setAttribute("message", "Projet supprimé avec succès.");
            } catch (NumberFormatException e) {
                request.setAttribute("message", "ID de projet invalide.");
            }
        } else if (action != null && action.equals("add")) {
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            LocalDate dateDebut = LocalDate.parse(request.getParameter("dateDebut"));
            LocalDate dateFin = LocalDate.parse(request.getParameter("dateFin"));
            String statut = request.getParameter("statut");

            String squadId = request.getParameter("squadId");

            Project newProject = new Project();
            newProject.setName(nom);
            newProject.setDescription(description);
            newProject.setStartDate(dateDebut);
            newProject.setEndDate(dateFin);
            newProject.setStatus(ProjectStatus.valueOf(statut));

            Squad squad = new Squad();
            squad.setId(Long.parseLong(squadId));
            newProject.setSquad(squad);


            errors = validator.validateProject(newProject);

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("project", newProject);
                List<Project> projects = projectService.getAllProjects();
                request.setAttribute("projects", projects);
                request.getRequestDispatcher("views/projects.jsp").forward(request, response);
                return;
            }

            projectService.addProject(newProject);
            request.setAttribute("message", "Projet ajouté avec succès !");
        }


        List<Project> projects = projectService.getAllProjects();
        request.setAttribute("projects", projects);
        request.getRequestDispatcher("views/projects.jsp").forward(request, response);
    }






}
