/**
 * Compare dois objetos JSON e retorne as diferenças.
 *
 * @param {Object} json1 Primeiro objeto JSON para comparação.
 * @param {Object} json2 Segundo objeto JSON para comparação.
 * @returns {Object} Objeto com as diferenças.
 */
function compareJSON (json1, json2) {
    const differences = {}
    function compareObjects (obj1, obj2, basePath = '') {
        Object.keys({ ...obj1, ...obj2 }).forEach(key => {
            const path = basePath ? `${basePath}.${key}` : key

            if (obj1[key] && !obj2.hasOwnProperty(key)) {
                differences[path] = { from: obj1[key], to: 'undefined' }
            } else if (obj2[key] && !obj1.hasOwnProperty(key)) {
                differences[path] = { from: 'undefined', to: obj2[key] }
            } else if (typeof obj1[key] === 'object' && typeof obj2[key] === 'object') {
                compareObjects(obj1[key], obj2[key], path)
            } else if (obj1[key] !== obj2[key]) {
                differences[path] = { from: obj1[key], to: obj2[key] }
            }
        })
    }

    compareObjects(json1, json2)

    return differences
}
let jsonAntigo = {}
let jsonNovo = {}

function findDifferences(json1, json2) {
  const obj1 = JSON.parse(JSON.stringify(json1));
  const obj2 = JSON.parse(JSON.stringify(json2));

  const differences = {};
  compareJsonElements(obj1, obj2, differences, '');

  return JSON.stringify(differences, null, 2); // Beautify the output
}

function compareJsonElements(element1, element2, differences, basePath) {
  Object.keys({...element1, ...element2}).forEach(key => {
      const path = basePath ? `${basePath}.${key}` : key;
      const value1 = element1[key];
      const value2 = element2[key];

      if (Array.isArray(value1) && Array.isArray(value2)) {
          compareJsonArrays(value1, value2, differences, path);
      } else if (typeof value1 === 'object' && typeof value2 === 'object' && value1 !== null && value2 !== null) {
          compareJsonElements(value1, value2, differences, path);
      } else if (value1 !== value2) {
          differences[path] = { from: value1, to: value2 };
      }
  });
}

function compareJsonArrays(arr1, arr2, differences, basePath) {
  arr1.forEach((obj1, index) => {
      const { idBanco: idBanco1, codBanco: codBanco1, isSelect: isSelect1, select: select1 } = obj1;
      let matchedIndex = arr2.findIndex(obj2 => {
          const { idBanco: idBanco2, codBanco: codBanco2, isSelect: isSelect2, select: select2 } = obj2;
          return (idBanco1 && idBanco1 === idBanco2) ||
                 (codBanco1 && codBanco1 === codBanco2) ||
                 ((isSelect1 === true || select1 === true) && (isSelect2 === true || select2 === true));
      });

      if (matchedIndex !== -1) {
          compareJsonElements(obj1, arr2[matchedIndex], differences, `${basePath}[${index}]`);
      } else if (!idBanco1 && !codBanco1 && (isSelect1 === true || select1 === true)) {
          // Specific case for new items not matched by idBanco or codBanco but have isSelect or select true
          differences[`${basePath}[${index}]`] = { from: obj1, to: 'undefined' };
      }
  });
}


const resultado = findDifferences(jsonAntigo,jsonNovo)
console.log(resultado)