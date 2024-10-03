<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="container mt-5">
    <h1 class="mb-4">Member List</h1>

    <button type="button" class="btn btn-primary mb-4" data-bs-toggle="modal" data-bs-target="#addMemberModal">Add New Member</button>

    <table class="table table-striped table-hover">
        <thead>
            <tr>
                <th>ID</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="member" items="${members}">
                <tr>
                    <td>${member.id}</td>
                    <td>${member.firstName}</td>
                    <td>${member.lastName}</td>
                    <td>${member.email}</td>
                    <td>${member.role}</td>
                    <td>
                        <a href="members?action=get&id=${member.id}" class="btn btn-info btn-sm">View</a>
                        <button type="button" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#updateMemberModal" 
                                data-id="${member.id}" 
                                data-firstname="${member.firstName}" 
                                data-lastname="${member.lastName}" 
                                data-email="${member.email}" 
                                data-role="${member.role}" 
                                data-squadid="${member.squadId}">Edit</button>
                        <form action="members" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${member.id}">
                            <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this member?');">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<!-- Form for adding -->
<div class="modal fade" id="addMemberModal" tabindex="-1" aria-labelledby="addMemberModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addMemberModalLabel">Add New Member</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="members" method="post">
                <input type="hidden" name="action" value="add">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="firstName" class="form-label">First Name</label>
                        <input type="text" class="form-control" id="firstName" name="first_name" required>
                    </div>
                    <div class="mb-3">
                        <label for="lastName" class="form-label">Last Name</label>
                        <input type="text" class="form-control" id="lastName" name="last_name" required>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="role" class="form-label">Role</label>
                        <select class="form-select" id="role" name="role" required>
                            <c:forEach var="role" items="${roles}">
                                <option value="${role}">${role}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="squadId" class="form-label">Squad</label>
                        <select class="form-select" id="squadId" name="squadId">
                            <option value="" selected>Select a squad (optional)</option>
                            <c:forEach var="squad" items="${squads}">
                                <option value="${squad.id}">${squad.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Save Member</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Form for Updating -->
<div class="modal fade" id="updateMemberModal" tabindex="-1" aria-labelledby="updateMemberModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="updateMemberModalLabel">Update Member</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="members" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="updateMemberId" name="id" value="">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="updateFirstName" class="form-label">First Name</label>
                        <input type="text" class="form-control" id="updateFirstName" name="first_name" required>
                    </div>
                    <div class="mb-3">
                        <label for="updateLastName" class="form-label">Last Name</label>
                        <input type="text" class="form-control" id="updateLastName" name="last_name" required>
                    </div>
                    <div class="mb-3">
                        <label for="updateEmail" class="form-label">Email</label>
                        <input type="email" class="form-control" id="updateEmail" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="updateRole" class="form-label">Role</label>
                        <select class="form-select" id="updateRole" name="role" required>
                            <c:forEach var="role" items="${roles}">
                                <option value="${role}">${role}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="updateSquadId" class="form-label">Squad</label>
                        <select class="form-select" id="updateSquadId" name="squadId">
                            <c:forEach var="squad" items="${squads}">
                                <option value="${squad.id}">${squad.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Update Member</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    const updateMemberModal = document.getElementById('updateMemberModal');
    updateMemberModal.addEventListener('show.bs.modal', (event) => {
        const button = event.relatedTarget; 
        const id = button.getAttribute('data-id'); 
        const firstName = button.getAttribute('data-firstname');
        const lastName = button.getAttribute('data-lastname');
        const email = button.getAttribute('data-email');
        const role = button.getAttribute('data-role');
        const squadId = button.getAttribute('data-squadid');

        const modalTitle = updateMemberModal.querySelector('.modal-title');
        const modalIdInput = updateMemberModal.querySelector('#updateMemberId');
        const modalFirstNameInput = updateMemberModal.querySelector('#updateFirstName');
        const modalLastNameInput = updateMemberModal.querySelector('#updateLastName');
        const modalEmailInput = updateMemberModal.querySelector('#updateEmail');
        const modalRoleInput = updateMemberModal.querySelector('#updateRole');
        const modalSquadInput = updateMemberModal.querySelector('#updateSquadId');

        modalTitle.textContent = `Update Member - ${firstName} ${lastName}`;
        modalIdInput.value = id;
        modalFirstNameInput.value = firstName;
        modalLastNameInput.value = lastName;
        modalEmailInput.value = email;
        modalRoleInput.value = role;
        modalSquadInput.value = squadId;
    });
</script>
