<script setup>
    import Header from "../ui/Header.vue";
    import Sidebar from "../ui/Sidebar.vue";
</script>

<template>
    <div class="wrapper relative">
        <Header title="Listagem de Alunas" />
        <Sidebar />

        <main class="p-10 w-full h-full relative">
            <slot><h1>oláaa</h1> </slot>
        </main>
    </div>
</template>

<style scoped>
    .wrapper {
        margin-left: calc(var(--spacing-sidebar) + 3vw);
        margin-right: 3vw;
    }

    main {
        min-height: calc(96vh - var(--spacing-header));
        margin-top: var(--spacing-header);
        border-radius: 24px;
        background-color: var(--color-card);
    }
</style>
