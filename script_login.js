const btnSignIn = document.getElementById('btn-sign-in');
const btnSignUp = document.getElementById('btn-sign-up');
const forms = document.getElementById('forms');
const sidebar = document.getElementById('sidebar');
const signIn = document.getElementById('sign-in');
const signUp = document.getElementById('sign-up');
const container = document.getElementById('container');
const linkSignIn = document.getElementById('link-sign-in');
const linkSignUp = document.getElementById('link-sign-up');

linkSignUp.addEventListener('click', ()=>{
    changeSignIn();
});

linkSignIn.addEventListener('click', ()=>{
    changeSignUp();
});

btnSignIn.addEventListener('click', ()=>{
    changeSignIn();
});

btnSignUp.addEventListener('click', ()=>{
    changeSignUp();
});

// Cambiar formulario Inicio de sesión 
function changeSignIn(){
    forms.classList.remove('active');
    sidebar.classList.remove('active');
    container.style.animation = 'none';
    container.style.animation = 'brounce-up 1s ease';
    transition(signIn);
}

// Cambiar formulario de registro
function changeSignUp(){
    forms.classList.add('active');
    sidebar.classList.add('active');
    container.style.animation = 'none';
    container.style.animation = 'brounce-down 1s ease';
    transition(signUp);
}

// Recibe un elemento y obtiene los hijos de ese elemento
function transition(parent){
    const children = parent.children;

    Array.from(children).forEach((child)=>{
        child.style.opacity = '0';
        child.style.animation = 'none';
    });

    setTimeout(() => {
        Array.from(children).forEach((child, index)=>{
            child.style.animation = 'slideIn 0.35s ease forwards';
            let delay = (index * 0.05)+'s';
            child.style.animationDelay = delay;
        });
    }, 300);
}