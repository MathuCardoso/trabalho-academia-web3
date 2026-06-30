<script setup>
    import { vMaska } from "maska/vue";
    import Errors from "./Errors.vue";

    const props = defineProps({
        model: String,
        value: String,
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
        <select
            :value="props.model"
            :name="props.model"
            :type="props.type"
            :placeholder="props.placeholder"
            v-maska="props.mask"
            @change="emit('updateValue', $event.target.value)"
        >
            <slot> </slot>
        </select>
        <Errors :error="props.error" />
    </div>
</template>

<style>
    .input-group {
        display: flex;
        flex-direction: column;
        gap: 8px;
        width: 100%;
        label {
            color: white;
        }
        select {
            color: white;
            border: 2px solid var(--color-pink-accent);
            border-radius: var(--radius-input);
            padding: 8px;
            transition: all 0.2s ease;
            width: v-bind("props.width");
            &:hover {
                box-shadow: 2px 2px 10px var(--color-pink-accent-shadow);
            }
            option {
                background-color: black;
                color: var(--color-pink-accent);
            }
        }
    }
</style>
