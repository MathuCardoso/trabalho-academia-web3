<script setup>
    import { XCircle } from "@lucide/vue";

    const props = defineProps({
        w: String,
        h: String,
        minW: String,
        minH: String,
    });

    const emit = defineEmits(["close"]);
</script>

<template>
    <div class="modal">
        <div class="modal-content relative z-50">
            <XCircle
                class="absolute right-3 top-3 cursor-pointer hover:text-danger transition"
                @click="emit('close')"
            />
            <slot> </slot>
        </div>
        <div class="absolute inset-0 z-0" @click="emit('close')"></div>
    </div>
</template>

<style>
    .modal {
        position: fixed;
        inset: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: rgba(0, 0, 0, 0.199);
        backdrop-filter: blur(5px);
        z-index: 50;
    }

    .modal-content {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        background: linear-gradient(
            120deg,
            rgba(0, 0, 0, 0.504),
            rgba(0, 0, 0, 0.84),
            rgba(0, 0, 0, 0.504)
        );
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
        border-radius: 20px;
        padding: 30px;
        min-width: v-bind("props.minW");
        min-height: v-bind("props.minH");
    }
    .modal-enter-active,
    .modal-leave-active {
        transition: opacity 0.3s ease;
    }

    .modal-enter-active .modal-content,
    .modal-leave-active .modal-content {
        transition:
            transform 0.3s ease,
            opacity 0.3s ease;
    }

    /* Estado inicial */
    .modal-enter-from {
        opacity: 0;
    }

    .modal-enter-from .modal-content {
        opacity: 0;
        transform: translateY(80px);
    }

    /* Estado final */
    .modal-enter-to {
        opacity: 1;
    }

    .modal-enter-to .modal-content {
        opacity: 1;
        transform: translateY(0);
    }

    /* Saída */
    .modal-leave-from {
        opacity: 1;
    }

    .modal-leave-from .modal-content {
        opacity: 1;
        transform: translateY(0);
    }

    .modal-leave-to {
        opacity: 0;
    }

    .modal-leave-to .modal-content {
        opacity: 0;
        transform: translateY(80px);
    }
</style>
