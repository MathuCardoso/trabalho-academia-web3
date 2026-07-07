<script setup>
    import { Search, X } from "@lucide/vue";

    const props = defineProps({
        model: {
            type: String,
            default: "",
        },
        placeholder: {
            type: String,
            default: "Pesquisar",
        },
    });

    const emit = defineEmits(["updateValue"]);
</script>

<template>
    <div class="search-input">
        <Search :size="19" />
        <input
            type="search"
            :value="props.model"
            :placeholder="props.placeholder"
            aria-label="Pesquisar registros"
            @input="emit('updateValue', $event.target.value)"
        />
        <button
            v-if="props.model"
            type="button"
            title="Limpar pesquisa"
            aria-label="Limpar pesquisa"
            @click="emit('updateValue', '')"
        >
            <X :size="18" />
        </button>
    </div>
</template>

<style scoped>
    .search-input {
        width: min(100%, 360px);
        height: 44px;
        padding: 0 12px;
        display: flex;
        align-items: center;
        gap: 9px;
        color: var(--color-pink-smooth);
        border: 2px solid var(--color-pink-accent);
        border-radius: var(--radius-input);
    }

    input {
        min-width: 0;
        flex: 1;
        color: white;
        outline: none;
    }

    input::-webkit-search-cancel-button {
        display: none;
    }

    button {
        display: flex;
        cursor: pointer;
    }
</style>
