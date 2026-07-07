<script setup>
    import Errors from "./Errors.vue";

    const props = defineProps({
        model: String,
        placeholder: String,
        name: String,
        label: String,
        rows: {
            type: Number,
            default: 4,
        },
        error: String,
    });

    const emit = defineEmits(["updateValue"]);
</script>

<template>
    <div class="textarea-group">
        <label :for="props.name">{{ props.label }}</label>
        <textarea
            :id="props.name"
            :name="props.name"
            :value="props.model"
            :rows="props.rows"
            :placeholder="props.placeholder"
            @input="emit('updateValue', $event.target.value)"
        ></textarea>
        <Errors :error="props.error" />
    </div>
</template>

<style scoped>
    .textarea-group {
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: 8px;
    }

    textarea {
        width: 100%;
        min-height: 104px;
        padding: 8px;
        color: white;
        border: 2px solid var(--color-pink-accent);
        border-radius: var(--radius-input);
        resize: vertical;
        transition: box-shadow 0.2s ease;
    }

    textarea:hover,
    textarea:focus {
        box-shadow: 2px 2px 10px var(--color-pink-accent-shadow);
        outline: none;
    }
</style>
