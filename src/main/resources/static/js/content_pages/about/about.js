const tabs = document.querySelectorAll(".tab");
const contents = document.querySelectorAll(".tabContent");

tabs.forEach((tab, index) => {
    tab.addEventListener("click", () => {
        tabs.forEach(t => t.classList.remove("active"));
        contents.forEach(c => c.classList.remove("active"));

        tab.classList.add("active");
        contents[index].classList.add("active");
    });
});