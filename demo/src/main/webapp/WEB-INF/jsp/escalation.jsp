<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>


    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Escalation Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
        <link rel="stylesheet" type="text/css" href="<c:url value='/css/escalation.css'/>"/>


</head>
<body>
    <!-- Header -->
    <header>
        <div class="logo">
            <img class="img" src="${pageContext.request.contextPath}/img/CRBIXLOGO.png" alt="Logo">
        </div>
    </header>

    <!-- Main Layout -->
    <div class="main-layout">
        <!-- Left Panel -->
        <div class="left-panel">
            <div class="profile-circle"></div>
            <p class="logocircleP" style="font-size:20px; margin-top:10px;">${username}</p>
            <div class="panel-box">
                In Time:<br> ${inTime}
            </div>
            <div class="panel-box">
                No of Tasks Assigned:<br> ${taskCount}
            </div>
        </div>

        <!-- Right Panel -->
        <div class="right-panel escalation-panel">

            <!-- Due Today -->
            <div class="column">
                <h3 class="column-title">Due Today</h3>
                <div class="task-list">
                    <c:forEach var="emp" items="${dueEmployees}">
                        <div class="task-card priority-p1"
                             data-bs-toggle="modal"
                             data-bs-target="#employeeTasksModal${emp.id}">
                            <span class="fw-bold">${emp.username}</span>
                            <span class="badge">${fn:length(dueTasksMap[emp.id])} Tasks</span>
                        </div>

                        <!-- Employee Task Modal -->
                        <div class="modal fade" id="employeeTasksModal${emp.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Tasks of ${emp.username}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <c:forEach var="task" items="${dueTasksMap[emp.id]}">
                                            <div class="d-flex justify-content-between align-items-center border-bottom py-2">
                                                <!-- Task Title Click = Details Modal -->
                                                <a href="#" class="task-detail-link fw-bold text-primary"
                                                   data-title="${task.title}"
                                                   data-summary="${task.summary}"
                                                   data-description="${task.description}">
                                                    ${task.title}
                                                </a>
                                                <div class="text-muted small text-end">
                                                    <div>Deadline: ${task.dueDate}</div>
                                                    <span class="badge bg-secondary">${task.status}</span>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty dueEmployees}">
                        <div class="no-task">No employees with due tasks</div>
                    </c:if>
                </div>
            </div>

            <!-- Escalated -->
            <div class="column">
                <h3 class="column-title">Escalated</h3>
                <div class="task-list">

                    <c:forEach var="emp" items="${escalatedEmployees}">
                        <div class="task-card priority-p2"
                             data-bs-toggle="modal"
                             data-bs-target="#employeeEscalationModal${emp.id}">
                            <span class="fw-bold">${emp.username}</span>
                            <span class="badge bg-danger">Count: ${escalationCountMap[emp.id]}</span>
                        </div>

                        <!-- Employee Escalation Modal -->
                        <div class="modal fade" id="employeeEscalationModal${emp.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Escalated Tasks of ${emp.username}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <c:forEach var="task" items="${escalatedTasksMap[emp.id]}">
                                            <div class="d-flex justify-content-between align-items-center border-bottom py-2">
                                                <!-- Task Title Click = Details Modal -->
                                                <a href="#" class="task-detail-link fw-bold text-primary"
                                                   data-title="${task.title}"
                                                   data-summary="${task.summary}"
                                                   data-description="${task.description}">
                                                    ${task.title}
                                                </a>
                                                <div class="text-muted small text-end">
                                                    <div>Deadline: ${task.dueDate}</div>
                                                    <span class="badge bg-secondary">${task.status}</span>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty escalatedEmployees}">
                        <div class="no-task">No employees with escalated tasks</div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- Back Button -->
    <a href="${pageContext.request.contextPath}/admin" class="back-btn">Back to Dashboard</a>

    <!-- Task Detail Modal (common for all tasks) -->
    <div class="modal fade" id="taskDetailModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Task Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p><strong>Title:</strong> <span id="taskTitle"></span></p>
                    <p><strong>Summary:</strong> <span id="taskSummary"></span></p>
                    <p><strong>Description:</strong> <span id="taskDescription"></span></p>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            document.querySelectorAll(".task-detail-link").forEach(function (element) {
                element.addEventListener("click", function (event) {
                    event.preventDefault();
                    document.getElementById("taskTitle").textContent = this.getAttribute("data-title");
                    document.getElementById("taskSummary").textContent = this.getAttribute("data-summary");
                    document.getElementById("taskDescription").textContent = this.getAttribute("data-description");
                    new bootstrap.Modal(document.getElementById("taskDetailModal")).show();
                });
            });
        });
    </script>
</body>
</html>
