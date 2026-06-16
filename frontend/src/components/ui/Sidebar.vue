<script setup>
    import { onMounted, ref } from "vue";
    import { useRoute } from "vue-router";

    const route = useRoute();

    const sideBarOptions = [
        {
            name: "Dashboard",
            route: "",
        },
        {
            name: "Alunas",
            route: "alunas",
        },
        {
            name: "Professoras",
            route: "professoras",
        },
        {
            name: "Treinos",
            route: "treinos",
        },
        {
            name: "Matrículas",
            route: "matriculas",
        },
        {
            name: "Frequência",
            route: "frequencias",
        },
    ];

    console.log(route.name === sideBarOptions[0])
</script>

<template>
    <div class="sidebar">
        <div class="brand">
            <h1 class="flex flex-col">
                Bella Fit
                <span class="text-pink-accent"> & Women </span>
            </h1>
        </div>
        <nav>
            <ul>
                <li
                    v-for="s in sideBarOptions"
                    :key="s.name"
                    :class="{ active: route.name === s.route }"
                >
                    {{ s.name }}
                </li>
            </ul>
        </nav>
    </div>
</template>

<style scoped>
    .sidebar {
        background-color: var(--color-card);
        border-top-right-radius: 50px;
        width: 15vw;
        height: 100vh;
    }

    nav {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-top: 20px;

        ul {
            display: flex;
            flex-direction: column;
            gap: 15px;

            li {
                padding: 15px;
                border-radius: 12px;
            }
            li.active {
                background-color: var(--color-pink-accent-shadow);
            }
        }
    }

    .brand {
        display: flex;
        justify-content: center;
        padding-top: 20px;
        font-size: 24px;
        font-family: "Playfair Display", serif;
    }
</style>
