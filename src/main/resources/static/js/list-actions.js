var currentPasswordActivity = 'edit';
var currentPasswordElementId = -1;
var currentDeleteElementId = -1;

function handleShowDetails(id, isEncrypted) {
    if (isEncrypted === 'false') {
        callShowDetails(id);
    } else {
        openPasswordModal('showDetails', id);
    }
}

function handleEdit(id, isEncrypted) {
    if (isEncrypted === 'false') {
        callEdit(id);
    } else {
        openPasswordModal('edit', id);
    }
}

function handleDelete(id) {
    currentDeleteElementId = id;
}

function handleModalDelete() {
    var form = document.getElementById("deleteForm" + currentDeleteElementId);
    form.submit();
}

function callShowDetails(id) {
    var form = document.getElementById("showDetailsForm" + id);
    form.submit();
}

function callEdit(id) {
    var form = document.getElementById("editForm" + id);
    form.submit();
}

function openPasswordModal(activity, elementId) {
    currentPasswordActivity = activity;
    currentPasswordElementId = elementId;
    $('#passwordModal').modal('show');
}

function handlePasswordInput() {
    var password = document.getElementById('passwordInput').value;

    // if (password.trim() !== '') {
    $('#passwordModal').modal('hide');

    if (currentPasswordActivity === 'edit') {
        document.getElementById('editFormPassword' + currentPasswordElementId).value = password;
        callEdit(currentPasswordElementId);
    } else if (currentPasswordActivity === 'showDetails') {
        document.getElementById('showDetailsFormPassword' + currentPasswordElementId).value = password;
        callShowDetails(currentPasswordElementId);
    }

    // } else {
    //     alert("Please enter a password.");
    // }
}

var passwordModalButtonSubmit = document.getElementById('password-modal-button-submit');
passwordModalButtonSubmit.addEventListener('click', function () {
    handlePasswordInput();
});

var deleteModalButtonSubmit = document.getElementById('delete-modal-button-submit');
deleteModalButtonSubmit.addEventListener('click', function () {
    handleModalDelete();
});


document.addEventListener('DOMContentLoaded', function () {
    var noteCards = document.querySelectorAll('.note-inner-card');
    noteCards.forEach(function (noteCard) {
        var noteId = noteCard.getAttribute('data-noteid');
        var isEncrypted = noteCard.getAttribute('data-isencrypted');

        noteCard.addEventListener('click', function () {
            handleShowDetails(noteId, isEncrypted);
        });

        var editButton = noteCard.querySelector('.edit-button');
        if (editButton != null) {
            editButton.addEventListener('click', function (event) {
                event.stopPropagation();
                handleEdit(noteId, isEncrypted);
            });
        }

        var deleteButton = noteCard.querySelector('.delete-button');
        if (deleteButton != null) {
            deleteButton.addEventListener('click', function (event) {
                event.stopPropagation();
                handleDelete(noteId);
            });
        }
    });
});
