<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Task Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/task.css'/>"/>
</head>
<body>
    <!-- Header -->
    <header>
        <div class="logo">
            <img class="img" src="${pageContext.request.contextPath}/img/CRBIXLOGO.png" alt="Logo">
        </div>
        <div class="logout" onclick="window.location='${pageContext.request.contextPath}/logout'">
            <p>LogOut</p>
        </div>
    </header>

<!-- Toast Notification Container -->
<!-- Toast Notification Container -->
<div id="toast-container">
    <c:set var="shown" value="false" />
    <c:forEach var="task" items="${tasks}">
        <c:if test="${task.status == 'Not Started' && !shown}">

            <div class="toast-notification">
                <p>
                    <strong>New Task Pending:</strong> ${task.title} <br/>
                    <small>Assigned By: ${task.assignedBy != null ? task.assignedBy.username : 'Admin'}</small>
                </p>
                <span class="toast-close" onclick="closeToast(this)">×</span>
                <form method="post" action="${pageContext.request.contextPath}/tasks/accept" style="display:inline;">
                    <input type="hidden" name="taskId" value="${task.id}">
         ``           <button type="submit" class="accept-toast">Accept</button>
                </form>
            </div>

            <c:set var="shown" value="true"/>
        </c:if>
    </c:forEach>
</div>


    <!-- Main Layout -->
    <div class="main-layout">
        <!-- Left Panel -->
        <div class="left-panel">
            <div class="profile-circle">
            <img src="img/profile.png" alt="Profile Photo" />
            </div>
            <p class="logocircleP" style="font-size:20px; margin-top:10px;">${name}</p>
            <div class="panel-box">
                In Time:<br> ${inTime}
            </div>
<!--
            <div class="panel-box">
                No of Tasks Assigned:<br> ${taskCount}
            </div>
-->
                              <!-- new -->
                                <div class="panel-box">No of Tasks Completed:<br>${CompletedTaskCount}</div>

        </div>

        <!-- Right Panel -->
        <div class="right-panel">
            <div class="filters">
                <select><option>Filter 1</option></select>
                <select><option>Filter 2</option></select>
                <select><option>Filter 3</option></select>
            </div>

            <!-- Tasks Container -->
            <div class="tasks">
                <c:forEach var="task" items="${tasks}" varStatus="status">
                    <div class="task-card priority-p${task.priority}"
                         onclick="openModal(this)"
                         data-id="${task.id}"
                         data-title="${task.title}"
                         data-summary="${task.summary}"
                         data-description="${task.description}"
                         data-priority="${task.priority}"
                         data-assignedby="${task.assignedBy != null ? task.assignedBy.username : 'Admin'}"
                         data-duedate="${task.dueDate != null ? task.dueDate : 'No Deadline'}"
                         data-status="${task.status}">
                        <p>Task ${status.index + 1}</p>
                        <p><strong>${task.title}</strong></p>
                        <p>Priority:
                            <c:choose>
                                <c:when test="${task.priority == 1}">Critical</c:when>
                                <c:when test="${task.priority == 2}">High</c:when>
                                <c:when test="${task.priority == 3}">Medium</c:when>
                                <c:when test="${task.priority == 4}">Low</c:when>
                                <c:when test="${task.priority == 5}">Optional/Backlog</c:when>
                                <c:otherwise>Unknown</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- Escalation Button -->
    <a href="${pageContext.request.contextPath}/emp/escalation" class="escalation-btn">View Escalations</a>

    <!-- Task Modal -->
    <div id="modal" class="modal-overlay">
        <div class="modal-content">
            <h2>Task Details</h2>
            <p><strong>Title:</strong> <span id="modalTitle"></span></p>
            <p><strong>Summary:</strong> <span id="modalSummary"></span></p>
            <p><strong>Description:</strong> <span id="modalDescription"></span></p>
            <p><strong>Assigned By:</strong> <span id="modalAssignedBy"></span></p>
            <p><strong>Priority:</strong> <span id="modalPriority"></span></p>
            <p><strong>Deadline:</strong> <span id="modalDeadline"></span></p>
            <form id="acceptForm" method="post" action="${pageContext.request.contextPath}/tasks/accept">
                <input type="hidden" name="taskId" id="taskIdField">
                <button type="submit" id="acceptButton">Accept</button>
            </form>
            <button onclick="closeModal()" style="margin-top:10px;">Close</button>
        </div>
    </div>

    <script>
        // Open modal function
        function openModal(card){
            document.getElementById('modal').style.display = 'flex';
            document.getElementById('modalTitle').innerText = card.getAttribute('data-title');
            document.getElementById('modalSummary').innerText = card.getAttribute('data-summary');
            document.getElementById('modalDescription').innerText = card.getAttribute('data-description');
            document.getElementById('modalAssignedBy').innerText = card.getAttribute('data-assignedby');
            document.getElementById('modalDeadline').innerText = card.getAttribute('data-duedate');

            const priorityMap = {1:"Critical",2:"High",3:"Medium",4:"Low",5:"Optional/Backlog"};
            document.getElementById('modalPriority').innerText = priorityMap[card.getAttribute('data-priority')];

            let status = card.getAttribute('data-status');
            let acceptButton = document.getElementById('acceptButton');
            if(status === "In Progress" || status === "Completed"){
                acceptButton.innerText = "Accepted";
                acceptButton.disabled = true;
            } else {
                acceptButton.innerText = "Accept";
                acceptButton.disabled = false;
            }
            document.getElementById('taskIdField').value = card.getAttribute('data-id');
        }

        function closeModal(){
            document.getElementById('modal').style.display = 'none';
        }

            function closeToast(span){
                const toast = span.parentElement;
                toast.remove();
                // Optional: reload the page to show next not-started task
                location.reload();
            }

    </script>
</body>
</html>
