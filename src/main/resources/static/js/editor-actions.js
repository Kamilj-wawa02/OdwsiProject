window.onload = function () {
    var content = document.getElementById('contentHidden');
    quill.root.innerHTML = content.value;
}

document.addEventListener('DOMContentLoaded', function () {
    toggleVisibleInputs();
});

function toggleVisibleInputs() {
    var isPublicChecked = document.getElementById('isPublic').checked;
    var isEncryptedChecked = document.getElementById('isEncrypted').checked;

    var isPublicSection = document.getElementById('isPublicSection');
    var isEncryptedSection = document.getElementById('isEncryptedSection');
    var passwordSection = document.getElementById('passwordSection');

    if (isPublicChecked) {
        isPublicSection.style.display = '';
        isEncryptedSection.style.display = 'none';
        passwordSection.style.display = 'none';
    } else {
        isEncryptedSection.style.display = '';
        if (isEncryptedChecked) {
            isPublicSection.style.display = 'none';
            passwordSection.style.display = '';
        } else {
            isPublicSection.style.display = '';
            passwordSection.style.display = 'none';
        }
    }
}

var quill = new Quill('#quill-editor', {
    theme: 'snow',
    placeholder: '   Write your content here...',
    modules: {
        toolbar: {
            container: [
                [{'header': [1, 2, 3, 4, 5, 6, false]}],
                ['bold', 'italic', 'underline', 'strike'],
                ['link', 'image'],
                ['clean']

            ],
            handlers: {
                image: function () {
                    var url = prompt('Enter the image URL:');
                    if (url) {
                        quill.focus();
                        var range = quill.getSelection();
                        quill.insertEmbed(range.index, 'image', url, Quill.sources.USER);
                    }
                }
            }
        }
    },
});

function submitForm() {
    document.getElementById('contentHidden').value = quill.root.innerHTML;
    document.getElementById('noteForm').submit();
}

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('isPublic')
        .addEventListener('change', function (event) {
            toggleVisibleInputs();
        });
    document.getElementById('isEncrypted')
        .addEventListener('change', function (event) {
            toggleVisibleInputs();
        });
    document.getElementById('submitButton')
        .addEventListener('click', function (event) {
            submitForm();
        });
});

