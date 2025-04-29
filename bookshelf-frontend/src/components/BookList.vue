<template>
  <div>
    <header>
      <h1>MY BOOKSHELF 2025</h1>
    </header>
    <div>
      <label for="goalInput">Objetivo de libros leídos:</label>
      <input
        type="number"
        v-model="goalInput"
        placeholder="Introduce un número"
        :disabled="goal > 0"
      />
      <button @click="setGoal" :disabled="goalInput <= 0 || goal > 0">Marcar objetivo</button> <!-- Deshabilita si ya hay un objetivo -->
    </div>
    <div class="books-read">
      <p>Libros leídos: {{ books.length }}</p>
    </div>
    <div class="progress-container">
      <p>Progreso: {{ progressPercentage }}%</p>
      <div class="progress-bar">
        <div class="progress" :style="{ width: progressPercentage + '%' }"></div>
      </div>
    </div>
    <div class="book-container">
      <div class="book" v-for="book in books" :key="book.id">
        <h3>{{ book.title }}</h3>
        <p><strong>Autor:</strong> {{ book.author }}</p>
        <p><strong>Género:</strong> {{ book.genre }}</p>
        <p><strong>Fecha de lectura:</strong> {{ book.readingDate }}</p>
        <p class="rating">⭐ {{ book.rating }}</p>
        <button @click="startEdit(book)" class="update-button">Actualizar</button>
      </div>
    </div>
    <div class="form-container">
      <div class="add-book-form">
        <h2>Añadir un nuevo libro</h2>
        <form @submit.prevent="addBook">
          <input type="text" v-model="newBook.title" placeholder="Título" required />
          <input type="text" v-model="newBook.author" placeholder="Autor" required />
          <input type="text" v-model="newBook.genre" placeholder="Género" required />
          <input type="date" v-model="newBook.readingDate" placeholder="Fecha de lectura" required />
          <input type="number" v-model="newBook.rating" placeholder="Calificación" min="1" max="5" required />
          <button type="submit" class="submit-button">¡Leído!</button>
        </form>
      </div>
      <div class="delete-book-form">
        <h2>Eliminar un libro</h2>
        <form @submit.prevent="confirmDeleteBook">
          <select v-model="bookToDelete" required>
            <option disabled value="">Selecciona un libro</option>
            <option v-for="book in books" :key="book.id" :value="book.id">{{ book.title }}</option>
          </select>
          <button type="submit" class="delete-button">Eliminar</button>
        </form>
      </div>
    </div>
    <div v-if="isEditing" class="modal-overlay">
      <div class="modal">
        <h2>Editar libro</h2>
        <form @submit.prevent="updateBook">
          <input type="text" v-model="bookToEdit.title" placeholder="Título" required />
          <input type="text" v-model="bookToEdit.author" placeholder="Autor" required />
          <input type="text" v-model="bookToEdit.genre" placeholder="Género" required />
          <input type="date" v-model="bookToEdit.readingDate" placeholder="Fecha de lectura" required />
          <input type="number" v-model="bookToEdit.rating" placeholder="Calificación" min="1" max="5" required />
          <button type="submit" class="submit-button">Actualizar libro</button>
          <button type="button" class="close-button" @click="closeModal">Cerrar</button>
        </form>
      </div>
    </div>
    <div v-if="confirmationMessage" :class="['confirmation-message', confirmationType === 'error' ? 'error' : 'success']">
      <svg
        v-if="confirmationType === 'success' || confirmationType === 'error'"
          class="icon-check"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 512 512"
          aria-hidden="true"
          focusable="false"
        >
        <path
          fill="currentColor"
          d="M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM369 209L241 337c-9.4 9.4-24.6 9.4-33.9 0l-64-64c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0l47 47L335 175c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9z"
        ></path>
      </svg>
      {{ confirmationMessage }}
    </div>
    <div v-if="showDeleteConfirm" class="confirmation-message" style="background-color: grey;">
      ¿Está seguro de que desea eliminar el libro?
      <div>
        <button @click="deleteBook" class="delete-button" style="background-color: #e74c3c;">Sí, eliminar</button>
        <button @click="cancelDelete" class="delete-button" style="background-color: black;">Cancelar</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "BookList",
  data() {
    return {
      books: [],  // Lista de libros
      newBook: {  // Datos del nuevo libro
        title: "",
        author: "",
        genre: "",
        readingDate: "",
        rating: null,
      },
      bookToDelete: "", // ID del libro a eliminar
      bookToEdit: null, // Datos del libro que se va a editar
      isEditing: false, // Estado para mostrar el formulario de edición
      confirmationMessage: "", // Mensaje de confirmación  
      confirmationType: "", 
      showDeleteConfirm: false,     
      goal: 0, 
      goalInput: "", 
    };
  },
  mounted() {
    this.fetchBooks();  // Cargar libros existentes
  },
  computed: {
    progressPercentage() {
    if (this.goal === 0 || this.books.length === 0) {
      return 0;  // Si no hay objetivo o no hay libros leídos, el progreso es 0
    }
    let percentage = (this.books.length / this.goal) * 100;
    return Math.min(percentage, 100).toFixed(2);  // Limita al 100% como máximo
  },
  },
  methods: {
    setGoal() {
    if (this.goalInput && !isNaN(this.goalInput)) {
        this.goal = parseInt(this.goalInput);  // Convertir goalInput a un número entero
    }
},
  fetchBooks() {
    fetch("http://localhost:8080/api/bookshelf")
      .then((res) => res.json())
      .then((data) => {
        this.books = data;
      })
      .catch((err) => console.error("Error:", err));
  },
  addBook() {
  fetch("http://localhost:8080/api/bookshelf", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(this.newBook),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error al guardar el libro");
      }
      this.fetchBooks();  // Actualizar la lista de libros
      this.confirmationMessage = "¡Libro añadido!";
      this.confirmationType = "success";
      setTimeout(() => {
        this.confirmationMessage = "";
      }, 3000);
      this.newBook = {
        title: "",
        author: "",
        genre: "",
        readingDate: "",
        rating: null,
      };
    })
    .catch((err) => console.error("Error:", err));
  },
  startEdit(book) {
    this.bookToEdit = { ...book };
    this.isEditing = true;
  },
  updateBook() {
    fetch(`http://localhost:8080/api/bookshelf/${this.bookToEdit.id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(this.bookToEdit),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Error al actualizar el libro");
        }
        const index = this.books.findIndex((book) => book.id === this.bookToEdit.id);
        this.books[index] = this.bookToEdit;
        this.closeModal();
        this.confirmationMessage = "Libro actualizado correctamente";
        this.confirmationType = "success";
        setTimeout(() => {
        this.confirmationMessage = "";
        }, 3000);
      })
      .catch((err) => console.error("Error:", err));
    },
    closeModal() {
      this.isEditing = false;
      this.bookToEdit = null;
    },
    confirmDeleteBook() {
        if (!this.bookToDelete) {
          return;
        }
        this.showDeleteConfirm = true;  // Mostrar confirmación de eliminación
      },
    deleteBook() {
      if (!this.bookToDelete) {
        return;
      }
      fetch(`http://localhost:8080/api/bookshelf/${this.bookToDelete}`, {
        method: "DELETE",
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Error al eliminar el libro");
          }
          this.books = this.books.filter((book) => book.id !== this.bookToDelete);
          this.bookToDelete = "";
          this.confirmationMessage = "Libro eliminado";
          this.confirmationType = "error";
          setTimeout(() => {
            this.confirmationMessage = "";
          }, 3000);
          this.showDeleteConfirm = false;  // Ocultar la confirmación
        })
        .catch((err) => console.error("Error:", err));
    },
    cancelDelete() {
      this.showDeleteConfirm = false;  // Ocultar la confirmación
    }
  }
};
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap');

body {
  font-family: 'Poppins', sans-serif;
  margin: 0;
  padding: 0;
  min-height: 100vh; 
  display: flex;
  flex-direction: column; 
  justify-content: flex-start; 
  background-color: #f4f4f4;
}

header {
  top: 0;
  left: 0;
  width: 100%;
  height: 200px;
  background-image: url('https://www.shutterstock.com/image-photo/banner-hardcover-books-row-isolated-600nw-2123120615.jpg');
  background-position: center;
  color: white;
  text-align: center;
  z-index: 1000;
}

header h1 {
  margin: 0;
  padding-top: 60px; 
  color: black;
}

.book-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  padding-top: 40px;
  width: 100%; 
}

.book {
  display: flex; 
  flex-direction: column;  
  background-color: #fff;
  border-radius: 8px;
  width: 250px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease-in-out;
  height: 260px;  /* Fijamos una altura para que todos los divs tengan el mismo tamaño */
}

.book:hover {
  transform: scale(1.05);
}

.book h3 {
  margin: 10px 0 5px;
  font-size: 1.2em;
  color: #333;
}

.book p {
  font-size: 0.9em;
  color: #555;
  margin: 5px 0;
}

.rating {
  font-size: 1.2em;
  color: #f39c12;
}

.form-container {
  display: flex;
  gap: 40px; 
  justify-content: center;
  margin-top: 40px;
}

.add-book-form {
  background-color: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  text-align: center;
  margin-bottom: 40px;
}

.add-book-form h2 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
}

.delete-book-form {
  background-color: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  text-align: center;
  margin-bottom: 40px;
}

.delete-book-form h2 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
}

.delete-book-form select {
  width: 100%;
  padding: 12px;
  margin-top: 5px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1em;
}

.delete-button {
  background-color: #e23134;
  width: 40%;
  padding: 12px;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
}

.delete-button:hover {
  background-color: #c31d20;
}

.submit-button {
  width: 40%;
  padding: 12px;
  background-color: black;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
}

.submit-button:hover {
  background-color: grey;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* Fondo semi-transparente */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 500px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.modal h2 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
  text-align: center;
}

.modal input {
  width: 100%;
  padding: 12px;
  margin-top: 5px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1em;
  box-sizing: border-box;
}

.submit-button {
  width: 40%;
  padding: 12px;
  background-color: black;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
  margin: 20px auto 10px; /* Centrado horizontal */
  display: block;  /* Hace que el botón sea un bloque para que se pueda centrar */
}

.submit-button:hover {
  background-color: grey;
}

.close-button {
  width: 40%;
  padding: 12px;
  background-color: #e23134;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 10px;
  margin: 20px auto 10px; /* Centrado horizontal */
  display: block;  /* Hace que el botón sea un bloque para que se pueda centrar */
}

.close-button:hover {
  background-color: #c31d20;
}

.update-button {
  margin-top: auto;  /* Empuja el botón hacia abajo dentro de su contenedor */
  padding: 12px 20px;
  background-color: black;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease, background-color 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.update-button:hover {
  background-color: grey;
  transform: scale(1.05);
}

.update-button:active {
  background-color: #1e7e34;
  transform: scale(1);
}

.confirmation-message {
  color: white;
  text-align: center;
  padding: 10px;
  border-radius: 5px;
  margin-top: 250px;
  font-size: 18px;
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
}

.confirmation-message.success {
  background-color: #2ecc71; /* verde */
}

.confirmation-message.error {
  background-color: #e74c3c; /* rojo */
}

.icon-check {
  width: 20px;
  height: 20px;
  margin-right: 10px;
  vertical-align: middle;
  fill: white;
}

.goal-input {
  width: 80%;
  padding: 10px;
  margin-top: 10px;
  border-radius: 5px;
  border: 1px solid #ddd;
}

.progress-container {
  width: 200px;
  text-align: center;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background-color: #ddd;
  border-radius: 5px;
  margin-top: 10px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #2ecc71;
  transition: width 0.3s ease;
}
.goal-container {
  margin-top: 20px;
}

.books-read {
  margin-top: 10px;
}

.progress-container {
  margin-top: 20px;
  width: 100%;
  max-width: 300px;
  text-align: center;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background-color: #ddd;
  border-radius: 5px;
  margin-top: 10px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #2ecc71;
  transition: width 0.3s ease;
}

</style>
