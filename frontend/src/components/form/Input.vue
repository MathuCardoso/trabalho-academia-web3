<script setup>
    import { vMaska } from "maska/vue";
    import Errors from "./Errors.vue";

    const props = defineProps({
        type: {
            type: String,
            default: "text",
        },
        placeholder: String,
        name: String,
        label: String,
        width: {
            type: String,
            default: "100%",
        },
        mask: String,
        error: String,
    });

    const emit = defineEmits(["updateValue"]);
</script>

<template>
    <div class="input-group">
        <label for="props.name">{{ props.label }}</label>
        <input
            :type="props.type"
            :placeholder="props.placeholder"
            :name="props.name"
            v-maska="props.mask"
            @input="emit('updateValue', $event.target.value)"
        />
        <Errors :error="props.error" />
    </div>
</template>

<style scoped>
    .input-group {
        display: flex;
        flex-direction: column;
        gap: 8px;
        width: 100%;
        label {
            color: white;
        }
        input {
            color: white;
            border: 2px solid var(--color-pink-accent);
            border-radius: var(--radius-input);
            padding: 8px;
            transition: all 0.2s ease;
            width: v-bind("props.width");
            &::placeholder {
                font-size: 0.9rem;
            }
            &:hover {
                box-shadow: 2px 2px 10px var(--color-pink-accent-shadow);
            }
        }
    }
</style>
