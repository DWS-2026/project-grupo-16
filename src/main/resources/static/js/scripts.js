/*!
* Start Bootstrap - Shop Homepage v5.0.6 (https://startbootstrap.com/template/shop-homepage)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-shop-homepage/blob/master/LICENSE)
*/
// This file is intentionally blank
// Use this file to add JavaScript to your project

document.addEventListener("DOMContentLoaded", function() {

    // ILUMINAR LA BARRA DE NAVEGACIÓN ACTIVA
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        // Si el href del enlace coincide con la URL actual, lo marcamos como activo
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active', 'fw-bold');
        }
    });

    // LOGICA PARA BORRAR ACTIVIDADES (Dashboard)
    document.querySelectorAll('.btn-delete-activity').forEach(button => {
        button.addEventListener('click', function() {
            const name = this.getAttribute('data-name');
            const id = this.getAttribute('data-id');
            if (confirm(`Are you sure you want to delete the activity "${name}"?\n\nThis action cannot be undone.`)) {
                window.location.href = '/activity/delete/' + id;
            }
        });
    });

    // LOGICA PARA BORRAR USUARIOS (Dashboard y Admin-Users)
    document.querySelectorAll('.btn-delete-user').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault(); // Evita que el enlace salte directamente si es un <a>
            const name = this.getAttribute('data-name') || 'this user';
            const id = this.getAttribute('data-id');
            if (confirm(`Are you sure you want to permanently delete ${name}?\n\nWarning: This cannot be undone.`)) {
                window.location.href = '/admin/user/delete/' + id;
            }
        });
    });

    // LOGICA PARA BORRAR RESEÑAS (Dashboard)
    document.querySelectorAll('.btn-delete-review').forEach(button => {
        button.addEventListener('click', function() {
            const className = this.getAttribute('data-name');
            const id = this.getAttribute('data-id');
            if (confirm(`Are you sure you want to delete the review for "${className}"?`)) {
                window.location.href = '/review/delete/' + id;
            }
        });
    });

    // LOGICA PARA BORRAR RESERVAS (Admin-Class)
    document.querySelectorAll('.btn-delete-booking').forEach(button => {
        button.addEventListener('click', function() {
            const userName = this.getAttribute('data-username');
            const bookingId = this.getAttribute('data-bookingid');
            const activityId = this.getAttribute('data-activityid');
            if (confirm(`Are you sure you want to remove ${userName} from this class?`)) {
                window.location.href = `/admin-class/booking/delete/${bookingId}?activityId=${activityId}`;
            }
        });
    });

    // Print Button (Admin-Class)
    const printBtn = document.getElementById('btnPrint');
    if (printBtn) {
        printBtn.addEventListener('click', () => window.print());
    }

    // BARRAS DE PROGRESO DINÁMICAS (Admin-Class) sin usar style="..."
    document.querySelectorAll('.dynamic-progress').forEach(bar => {
        const width = bar.getAttribute('data-width');
        bar.style.width = width + '%';
    });
});

// 8. LOGICA DEL SELECTOR DE CLASES (Admin-Class)
    const classSelector = document.getElementById('classSelector');
    if (classSelector) {
        // Lee el ID de la actividad seleccionada y la marca en el desplegable
        const selectedId = classSelector.getAttribute('data-selected-id');
        if (selectedId) {
            classSelector.value = selectedId;
        }

        // Cuando cambies de clase, el formulario se envía automáticamente
        classSelector.addEventListener('change', function() {
            this.form.submit();
        });
    };

document.addEventListener("DOMContentLoaded", function() {

    // --- LÓGICA DE QUILL.JS PARA ACTIVIDADES ---
    var editorContainer = document.getElementById('editor-container');

    if (editorContainer) {
        // Inicializamos el editor con los botones deseados
        var quill = new Quill('#editor-container', {
            theme: 'snow',
            placeholder: 'Write the activity description here...',
            modules: {
                toolbar: [
                    ['bold', 'italic', 'underline'],
                    [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                    ['clean']
                ]
            }
        });

        var form = document.getElementById('activityForm');
        var hiddenDescription = document.getElementById('hiddenDescription');

        // Si hay texto guardado (al editar), lo cargamos en el editor
        if (hiddenDescription && hiddenDescription.value) {
            quill.root.innerHTML = hiddenDescription.value;
        }

        // Al enviar el formulario, volcamos el HTML seguro del editor al textarea
        if(form) {
            form.onsubmit = function() {
                hiddenDescription.value = quill.root.innerHTML;
            };
        }
    }
});

