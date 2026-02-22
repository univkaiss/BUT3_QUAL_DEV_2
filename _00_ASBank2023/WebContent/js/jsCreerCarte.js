function validateCreerCarteForm() {
    const expMois = document.getElementById("expMois").value;
    const expAnnee = document.getElementById("expAnnee").value;
    const last4 = document.getElementById("last4").value;

    const errDiv = document.getElementById("clientError");
    if (errDiv) errDiv.innerHTML = "";

    const trimmedExpMois = (expMois || "").trim();
    const trimmedExpAnnee = (expAnnee || "").trim();
    const trimmedLast4 = (last4 || "").trim();

    if (!/^\d+$/.test(trimmedExpMois)) return showErr("Le mois d'expiration doit être un nombre (1 à 12).");
    const m = Number.parseInt(trimmedExpMois, 10);
    if (m < 1 || m > 12) return showErr("Le mois d'expiration doit être compris entre 1 et 12.");

    if (!/^\d+$/.test(trimmedExpAnnee)) return showErr("L'année d'expiration doit être un nombre (ex: 2030).");
    const y = Number.parseInt(trimmedExpAnnee, 10);
    if (y < 1900) return showErr("L'année d'expiration semble invalide.");

    if (!/^\d{4}$/.test(trimmedLast4)) return showErr("Les 4 derniers chiffres doivent contenir exactement 4 chiffres.");

    return true;

    function showErr(msg) {
        if (errDiv) errDiv.innerHTML = msg;
        else alert(msg);
        return false;
    }
}