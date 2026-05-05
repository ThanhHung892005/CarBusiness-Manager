// Car Management System - Main JS

$(document).ready(function () {
    // Auto-dismiss alerts after 5 seconds
    setTimeout(function () {
        $('.alert.alert-success, .alert.alert-danger').fadeOut('slow');
    }, 5000);

    // Confirm delete/disable dialogs
    $('[data-confirm]').on('submit', function (e) {
        if (!confirm($(this).data('confirm'))) {
            e.preventDefault();
        }
    });

    // Format currency inputs
    $('input[data-format="currency"]').on('blur', function () {
        const val = parseFloat($(this).val().replace(/,/g, ''));
        if (!isNaN(val)) {
            $(this).val(val.toLocaleString('vi-VN'));
        }
    });
});

// Utility: format number as VND
function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN').format(amount) + ' ₫';
}
