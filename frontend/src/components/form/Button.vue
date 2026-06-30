<script setup>
    const props = defineProps({
        variant: String,
        class: String,
        type: {
            type: String,
            default: "button",
        },
        color: {
            type: String,
            default: "var(--color-pink-accent)",
        },
        bg: {
            type: String,
            default: "var(--color-near-black)",
        },
    });
</script>
<template>
    <button
        :type="props.type"
        :class="[props.variant ,props.class]"
        class="flex justify-center items-center h-fit transition-all duration-200 text-center"
    >
        <span class="relative">
            <slot> </slot>
        </span>
        <slot name="icon"> </slot>
    </button>
</template>

<style scoped>
    button {
        font-size: 1.1rem;
        color: v-bind("props.color");
        background-color: v-bind("props.bg");
        padding: 10px 16px;
        border-radius: var(--radius-button);
        cursor: pointer;
        width: 100%;

        &:active {
            transform: translateY(0px);
        }
    }

    button.info {
        color: white;
        background-color: var(--color-info);
    }

    button.success {
        color: white;
        background-color: var(--color-success);
    }

    button.danger {
        color: white;
        background-color: var(--color-danger);
    }
</style>
