<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Employee Escalation</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/emp-escalation.css'/>"/>
</head>
<body>
    <!-- Header -->
    <header>
      <div class="logo">
          <a href="${pageContext.request.contextPath}/tasks">
              <img class="img" src="${pageContext.request.contextPath}/img/CRBIXLOGO.png" alt="Logo">
          </a>
      </div>


    </header>

    <!-- Main Layout -->
    <div class="main-layout">
        <!-- Left Panel -->
        <div class="left-panel">
 <div class="profile-circle">
<img src="${pageContext.request.contextPath}/img/profile.png" alt="Profile Photo" />
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


            <!--new -->
            <div class="panel-box">No of Tasks Completed:<br>${CompletedTaskCount}</div>


        </div>

        <!-- Right Panel -->
        <div class="right-panel escalation-panel">


            <div class="column">

                <h3 class="column-title">Due Tasks</h3>
                <div class="task-list">
                    <c:forEach var="task" items="${dueTasks}">
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
                            <p><strong>${task.title}</strong></p>
                            <span class="badge">${task.status}</span>
                        </div>
                    </c:forEach>
                    <c:if test="${empty dueTasks}">
                        <div class="no-task">No due tasks</div>
                    </c:if>
                </div>
            </div>

            <div class="column">
                <h3 class="column-title">Escalated Tasks</h3>
                <div class="task-list">
                    <c:forEach var="task" items="${escalatedTasks}">
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
                            <p><strong>${task.title}</strong></p>
                            <span class="badge">${task.status}</span>
                        </div>
                    </c:forEach>
                    <c:if test="${empty escalatedTasks}">
                        <div class="no-task">No escalated tasks</div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- Back Button -->
    <a href="${pageContext.request.contextPath}/tasks" class="back-btn">Back to Dashboard</a>

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
        // Open modal function (same as task.jsp)
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
    </script>
</body>
</html>
