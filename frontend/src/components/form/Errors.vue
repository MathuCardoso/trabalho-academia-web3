<script setup>
    const props = defineProps({
        error: String,
    });
</script>

<template>
    <span v-if="props.error">{{ props.error }}</span>
</template>

<style scoped>
    span {
        width: 100%;
        color: var(--color-danger);
        font-size: .9rem;
    }
</style>
