<script setup>
    const props = defineProps({
        type: {
            type: String,
            default: "text",
        },
        placeholder: String,
        name: String,
        label: String,
    });
</script>

<template>
    <div class="input-group">
        <label for="props.name">{{ props.label }}</label>
        <input
            :type="props.type"
            :placeholder="props.placeholder"
            :name="props.name"
        />
    </div>
</template>

<style scoped>
    .input-group {
        display: flex;
        flex-direction: column;
        gap: 8px;
        label {
            color: white;
        }
        input {
            color: white;
            border: 2px solid var(--color-pink-accent);
            border-radius: var(--radius-input);
            padding: 8px;
            transition: all 0.2s ease;
            &:hover {
                box-shadow: 2px 2px 10px var(--color-pink-accent-shadow);
            }
        }
    }
</style>
