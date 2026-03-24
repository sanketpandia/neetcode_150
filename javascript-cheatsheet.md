# JavaScript Cheat Sheet

## Variables

```js
var name = "old style";       // function-scoped, avoid
let age = 25;                 // block-scoped, reassignable
const PI = 3.14;              // block-scoped, not reassignable
let x;                        // undefined by default
```

## Data Types

```js
let str = "hello";            // String
let num = 42;                 // Number
let bool = true;              // Boolean
let nothing = null;           // Null
let undef = undefined;        // Undefined
let sym = Symbol("id");       // Symbol
let big = 9007199254740991n;  // BigInt
let obj = { key: "value" };   // Object
```

## Arrays

```js
let arr = [1, 2, 3];
arr.push(4);                  // add to end       → [1,2,3,4]
arr.pop();                    // remove from end   → [1,2,3]
arr.unshift(0);               // add to start      → [0,1,2,3]
arr.shift();                  // remove from start  → [1,2,3]
arr.splice(1, 1);             // remove at index 1  → [1,3]
arr.splice(1, 0, 2);          // insert 2 at index 1 → [1,2,3]
arr.includes(2);              // true
arr.indexOf(3);               // 2
arr.length;                   // 3
let copy = [...arr];          // spread copy
let merged = [...arr, ...arr];// concat
```

## Iterating Arrays

```js
// for loop
for (let i = 0; i < arr.length; i++) { console.log(arr[i]); }

// for...of (values)
for (const val of arr) { console.log(val); }

// forEach
arr.forEach((val, i) => console.log(i, val));

// map – returns new array
const doubled = arr.map(x => x * 2);

// filter – returns matching items
const evens = arr.filter(x => x % 2 === 0);

// reduce – accumulate to single value
const sum = arr.reduce((acc, x) => acc + x, 0);

// find / findIndex
const found = arr.find(x => x > 1);
const idx = arr.findIndex(x => x > 1);
```

## Objects

```js
const person = { name: "Alice", age: 30 };
person.name;                  // dot access
person["age"];                // bracket access
person.email = "a@b.com";    // add property
delete person.email;          // remove property
Object.keys(person);          // ["name", "age"]
Object.values(person);        // ["Alice", 30]
Object.entries(person);       // [["name","Alice"], ["age",30]]
const { name, age } = person; // destructuring

// iterate
for (const [key, val] of Object.entries(person)) { console.log(key, val); }
```

## Functions

```js
function greet(name) { return `Hello, ${name}!`; }       // declaration
const greet2 = function(name) { return `Hi, ${name}`; };  // expression
const greet3 = (name) => `Hey, ${name}`;                  // arrow
const greet4 = (name = "World") => `Hello, ${name}`;      // default param
```

## Classes

```js
class Animal {
  static count = 0;               // class/static variable

  constructor(name) {
    this.name = name;              // instance variable
    Animal.count++;
  }

  speak() {                        // instance method
    return `${this.name} makes a sound.`;
  }

  static getCount() {              // static method
    return Animal.count;
  }
}

class Dog extends Animal {         // inheritance
  #breed;                          // private field

  constructor(name, breed) {
    super(name);                   // call parent constructor
    this.#breed = breed;
  }

  speak() { return `${this.name} barks.`; }  // override
  get info() { return `${this.name} (${this.#breed})`; }  // getter
}

const d = new Dog("Rex", "Lab");
d.speak();                         // "Rex barks."
d.info;                            // "Rex (Lab)"
Animal.getCount();                 // 1
```

## Strings

```js
let s = "Hello, World!";
s.length;                     // 13
s.toUpperCase();              // "HELLO, WORLD!"
s.slice(0, 5);                // "Hello"
s.split(", ");                // ["Hello", "World!"]
s.includes("World");          // true
s.replace("World", "JS");    // "Hello, JS!"
`template ${s}`;              // template literal
```

## Control Flow

```js
// if / else
if (x > 0) { /**/ } else if (x === 0) { /**/ } else { /**/ }

// ternary
const label = x > 0 ? "positive" : "non-positive";

// switch
switch (color) {
  case "red":   /**/; break;
  case "blue":  /**/; break;
  default:      /**/;
}

// loops
while (cond) { /**/ }
do { /**/ } while (cond);
for (let i = 0; i < 10; i++) { /**/ }
for (const key in obj) { /**/ }    // object keys
for (const val of iterable) { /**/ } // iterable values
```

## Promises & Async/Await

```js
// Promise
fetch(url)
  .then(res => res.json())
  .then(data => console.log(data))
  .catch(err => console.error(err));

// async/await
async function getData() {
  try {
    const res = await fetch(url);
    const data = await res.json();
    return data;
  } catch (err) { console.error(err); }
}
```

## Common Patterns

```js
// nullish coalescing & optional chaining
const val = obj?.nested?.prop ?? "default";

// spread & rest
const merged = { ...obj1, ...obj2 };
function sum(...nums) { return nums.reduce((a, b) => a + b, 0); }

// destructuring assignment
const [a, b, ...rest] = [1, 2, 3, 4];
const { x, y = 10 } = { x: 5 };

// map & set
const map = new Map([["a", 1]]);  map.set("b", 2);  map.get("a");
const set = new Set([1, 2, 2]);   set.add(3);        set.has(1);
```
