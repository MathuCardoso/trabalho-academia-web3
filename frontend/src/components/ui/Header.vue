<script setup lang="ts">
    import { inject } from "vue";
    import UserIcon from "../icons/UserIcon.vue";
    const title = inject("headerTitle");
    const description = inject("headerDescription");
</script>

<template>
    <header>
        <div class="welcome">
            <h2>{{ title }}</h2>
            <p v-if="description">{{ description }}</p>
        </div>
        <nav>
            <ul>
                <li><UserIcon /></li>
                <li>Opção 2</li>
                <li>Opção 3</li>
            </ul>
        </nav>
    </header>
</template>

<style scoped>
    header {
        padding-left: calc(var(--spacing-sidebar) + 3vw);
        padding-right: 3vw;
        position: fixed;
        top: 0;
        left: 0;
        height: var(--spacing-header);
        width: 100%;
        display: flex;
        z-index: 20;
        display: flex;
        justify-content: space-between;
        align-items: center;
        backdrop-filter: blur(4px);

        h2 {
            font-size: 24px;
            font-weight: bold;
        }
    }

    ul {
        display: flex;
        gap: 15px;
    }
</style>
