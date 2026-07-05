<script setup>
    import MainLayout from "@/components/layout/MainLayout.vue";
    import { useAuthStore } from "@/stores/authStore";
    import { provide } from "vue";

    const auth = useAuthStore();

    provide("headerTitle", "Olá, " + auth.usuario.nome);
    provide("headerDescription", "Bem-vindo ao sistema de gestão de academia");
</script>

<template>
    <MainLayout> </MainLayout>
</template>

<style scoped></style>
