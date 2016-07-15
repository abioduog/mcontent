/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function check() {
    if(document.getElementById('password01').value === document.getElementById('password02').value) {
 document.getElementById('password02').style.backgroundColor = "#90EE90";

    } else {
 document.getElementById('password02').style.backgroundColor = "#E8ADAA";
    }
}

function checkPassword() {


if(document.getElementById('password01').value.length < 8 || document.getElementById('password01').value.length > 16 ){
         mContent.alert.error("Password length must be between 8 and 16 characters");
return false;
}

    if(document.getElementById('password01').value !== document.getElementById('password02').value) {
         mContent.alert.error("Passwords doesn\'t match");
return false;

}
return true;
}


