<script setup>
    const props = defineProps({
        content: {
            type: String,
            default: "Salvar",
        },
        type: {
            type: String,
            default: "button",
        },
    });
</script>
<template>
    <button :type="props.type">
        {{ props.content }}
    </button>
</template>

<style scoped>
    button {
        font-size: 18px;
        color: white;
        background-color: var(--color-near-black);
        padding: 8px;
        border-radius: var(--radius-card);
        cursor: pointer;
        transition: transform 0.2s ease-out;

        &:hover {
            transform: translateY(-2px);
        }

        &:active {
            transform: translateY(0px);
        }
    }
</style>
