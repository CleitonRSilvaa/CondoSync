const items = [
    // USUÁRIO ADMIN
    {
        link: "/admin/mural.html",
        icon: "fas fa-bullhorn",
        titulo: "Gerenciar Mural",
        texto: "Administre os anúncios do condomínio."
    },
    {
        link: "/admin/gerenciar-moradores.html",
        icon: "fas fa-users",
        titulo: "Cadastro de moradores",
        texto: "Registre e atualize informações dos moradores."
    },
    {
        link: "/admin/gerenciar-reservas.html",
        icon: "fas fa-calendar-check",
        titulo: "Agendamentos",
        texto: "Gerencie as reservas das áreas comuns."
    },

    // USUÁRIO PADRÃO
    {
        link: "/morador/perfil/index.html",
        icon: "fas fa-home",
        titulo: "Meu Apartamento",
        texto: "Veja e edite os dados do seu apartamento."
    },
    {
        link: "/morador/mural/index.html",
        icon: "fas fa-info-circle",
        titulo: "Informativo",
        texto: "Acesse as notícias e comunicados."
    },
    {
        link: "/morador/financeiro/index.html",
        icon: "fas fa-dollar-sign",
        titulo: "Financeiro",
        texto: "Consulte suas despesas e pagamentos."
    },
    {
        link: "/morador/Ocorrencia/index.html",
        icon: "fas fa-life-ring",
        titulo: "Suporte",
        texto: "Solicite ajuda e tire suas dúvidas."
    },
    {
        link: "/morador/espaco-agendamento/index.html",
        icon: "fas fa-calendar-alt",
        titulo: "Agendamento",
        texto: "Reserve espaços e serviços do condomínio."
    },
];



const lista = document.querySelector('.lista');
items.forEach(item => {
    const a = document.createElement('a');
    a.href = item.link;
    a.classList.add('item');

    const button = document.createElement('button');
    const div = document.createElement('div');

    const i = document.createElement('i');
    item.icon.split(' ').forEach(cls => i.classList.add(cls));

    const h4 = document.createElement('h4');
    h4.textContent = item.titulo;

    const h6 = document.createElement('h6');
    h6.textContent = item.texto;

    button.appendChild(i);

    div.appendChild(h4);
    div.appendChild(h6);

    a.appendChild(button);
    a.appendChild(div);

    lista.appendChild(a);

    const separador = document.createElement('div');
    separador.classList.add('separador');
    lista.appendChild(separador);
});