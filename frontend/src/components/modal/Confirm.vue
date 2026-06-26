<script setup>
    import Button from "../form/Button.vue";
    import Modal from "./Modal.vue";

    const props = defineProps({
        message: String,
        title: String,
    });

    const emit = defineEmits(["close", "confirm"]);
</script>

<template>
            <Modal @close="emit('close')" min-w="500px" min-h="25vh">
                <h1
                    class="text-center w-full text-2xl font-bold mb-4 text-pink-accent -mt-2"
                >
                    {{ props.title }}
                </h1>
                <span class="text-2xl">{{ props.message }}</span>
                <div class="flex gap-6 mt-4 w-full">
                    <Button variant="success" @click="emit('confirm')"
                        >Confirmar</Button
                    >
                    <Button @click="emit('close')">Cancelar</Button>
                </div>
            </Modal>
</template>
