const charSetPatterns = [
    /[a-z]/,
    /[A-Z]/,
    /[0-9]/,
    /[^a-zA-Z0-9]/
];

const charSetSizes = [26, 26, 10, 33];

function calculatePasswordEntropy(password) {
    let entropy = 0;

    for (let i = 0; i < charSetPatterns.length; i++) {
        if (charSetPatterns[i].test(password)) {
            entropy += Math.log2(charSetSizes[i]);
        }
    }

    return Math.round(entropy * password.length);
}

function checkEntropy() {
    const passwordInput = document.getElementById("password");
    const resultDiv = document.getElementById("result");
    const strengthBar = document.getElementById("password-strength-bar");

    const password = passwordInput.value;
    const entropy = calculatePasswordEntropy(password);

    if (entropy > 80) {
        resultDiv.innerHTML = "<span class='text-success'>Strong password</span>";
        strengthBar.classList.remove("bg-danger");
        strengthBar.classList.remove("bg-warning");
        strengthBar.classList.add("bg-success");
    } else if (entropy >= 60) {
        resultDiv.innerHTML = "<span class='text-warning'>Password still not strong enough</span>";
        strengthBar.classList.remove("bg-danger");
        strengthBar.classList.add("bg-warning");
        strengthBar.classList.remove("bg-success");
    } else {
        if (entropy === 0) {
            resultDiv.innerHTML = "";
        } else {
            resultDiv.innerHTML = "<span class='text-danger'>Weak password</span>";
        }
        strengthBar.classList.add("bg-danger");
        strengthBar.classList.remove("bg-warning");
        strengthBar.classList.remove("bg-success");
    }
}

const passwordInput = document.getElementById("password");
passwordInput.addEventListener("input", checkEntropy);
passwordInput.addEventListener("change", checkEntropy);