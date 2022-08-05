
const mapNodeSize = (nodes, propertyName, visualRange) => {
    let minp = 9999999999;
    let maxp = -9999999999;
    nodes.forEach((node) => {
      node[propertyName] = Math.pow(node[propertyName], 1 / 3);
      minp = node[propertyName] < minp ? node[propertyName] : minp;
      maxp = node[propertyName] > maxp ? node[propertyName] : maxp;
    });
    const rangepLength = maxp - minp;
    const rangevLength = visualRange[1] - visualRange[0];
    nodes.forEach((node) => {
      node.size = ((node[propertyName] - minp) / rangepLength) * rangevLength + visualRange[0];
    });
  };
  /*
  const container = document.getElementById('g6-container');
  const descriptionDiv = document.createElement('div');
  descriptionDiv.innerHTML = 'Graph 6 Example';
  container.appendChild(descriptionDiv);
  
  const width = container.scrollWidth;
  const height = container.scrollHeight || 500;
  const graph = new G6.Graph({
    container: 'g6-container',
    width,
    height: 700,
    defaultNode: {
      size: 2,
      style: {
        fill: '#C6E5FF',
        stroke: '#5B8FF9',
        lineWidth: 0.3,
      },
      labelCfg: {
        style: {
          fontSize: 3,
        },
        position: 'right',
        offset: 1,
      },
    },
    defaultEdge: {
      size: 0.1,
      color: '#333',
      type: 'line',
    },
    nodeStateStyles: {
      selected: {
        fill: 'steelblue',
        stroke: '#000',
        lineWidth: 1,
      },
      hover: {
        fill: 'red',
        stroke: '#000',
        lineWidth: 1,
      },
    },
    modes: {
      default: [
        {
          type: 'zoom-canvas',
          enableOptimize: true,
          optimizeZoom: 0.9,
        },
        {
          type: 'drag-canvas',
          enableOptimize: true,
        },
        'drag-node',
        'brush-select',
      ], // 'drag-canvas',
    },
  });
  
  fetch('https://gw.alipayobjects.com/os/basement_prod/0b9730ff-0850-46ff-84d0-1d4afecd43e6.json')
    .then((res) => res.json())
    .then((data) => {
      data.nodes.forEach((node) => {
        node.label = node.olabel;
        node.labelCfg.style = {
          fontSize: 1.3,
        };
        node.degree = 0;
        data.edges.forEach((edge) => {
          if (edge.source === node.id || edge.target === node.id) {
            node.degree++;
          }
        });
      });
      console.log('原始数据', data.nodes.length, data.edges.length);
      mapNodeSize(data.nodes, 'degree', [1, 15]);
      // console.log(data.nodes);
      graph.data(data);
      graph.render();
      graph.on('node:mouseenter', (e) => {
        const { item } = e;
        graph.setItemState(item, 'hover', true);
      });
      graph.on('node:mouseleave', (e) => {
        const { item } = e;
        graph.setItemState(item, 'hover', false);
      });
  
      const graphData = graph.save();
      const nodeLen = graphData.nodes.length;
      const edgeLen = graphData.edges.length;
      descriptionDiv.innerHTML = `节点数量：${nodeLen}, 边数量：${edgeLen}, 图元数量：${
        nodeLen * 2 + edgeLen
      }`;
    });
  
  if (typeof window !== 'undefined')
    window.onresize = () => {
      if (!graph || graph.get('destroyed')) return;
      if (!container || !container.scrollWidth || !container.scrollHeight) return;
      graph.changeSize(container.scrollWidth, container.scrollHeight);
    };
    */




    const fittingString = (str, maxWidth, fontSize) => {
        let currentWidth = 0;
        let res = str;
        const pattern = new RegExp('[\u4E00-\u9FA5]+'); // distinguish the Chinese charactors and letters
        str.split('').forEach((letter, i) => {
          if (currentWidth > maxWidth) return;
          if (pattern.test(letter)) {
            // Chinese charactors
            currentWidth += fontSize;
          } else {
            // get the width of single letter according to the fontSize
            currentWidth += G6.Util.getLetterWidth(letter, fontSize);
          }
          if (currentWidth > maxWidth) {
            res = `${str.substr(0, i)}\n${str.substr(i)}`;
          }
        });
        return res;
      };
      
      const globalFontSize = 12;
    const data = {
        nodes : [ {
            "id" : "91f774e118e1a669195b0c0af5bcc203",
            "label" : "Cemtrex Inc.",
            "level" : "tracking",
            "color" : "red",
            "size" : 35
          },{
            "id" : "a33f07b4bea01407b7e8c0107d2b034a",
            "label" : "Oculus",
            "level" : "tracking",
            "color" : "red",
            "size" : 35
          }, {
            "id" : "fc6dfb0be699c21d4a2af70c4f1c2920",
            "label" : "HTC CORPORATION",
            "level" : "tracking",
            "color" : "red",
            "size" : 35
          }, {
            "id" : "9f9499d043b79a41b967c5a0729ad591",
            "label" : "AlphaSense",
            "level" : "tracking",
            "color" : "red",
            "size" : 35
          }, {
            "id" : "2fe9ab5bcfbfc52a9ec69d4f7890b790",
            "label" : "Advanced Micro Devices Inc.",
            "level" : "tracking",
            "color" : "red",
            "size" : 35
          }, {
            "id" : "f2e9fe1494dc237dde91fdbe979b05ab",
            "label" : "Magic Leap",
            "level" : "surfacing",
            "color" : "orange",
            "size" : 35
          }, {
            "id" : "44b2135733688346aa24f67ea8895839",
            "label" : "NVIDIA Corporation",
            "level" : "tracking",
            "color" : "red",
            "size" : 35
          }, {
            "id" : "b8dfe6ba-5ed6-8875-fb17-cf90e3bc40b1",
            "label" : "Vyoocam",
            "level" : "surfacing",
            "color" : "orange",
            "size" : 35
          }, {
            "id" : "d3ea63bf-e881-e5f9-1531-cf2c22c2508b",
            "label" : "Gestures",
            "level" : "surfacing",
            "color" : "orange",
            "size" : 35
          }, 
        ],
        edges : [ {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "d3ea63bf-e881-e5f9-1531-cf2c22c2508b",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "fbca7a0a-3446-43a6-a66a-776e3c89a5d1",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "cbdc5e2a-925a-46c7-860b-f1298e2b25b4",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "b8dfe6ba-5ed6-8875-fb17-cf90e3bc40b1",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "44b2135733688346aa24f67ea8895839",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "f2e9fe1494dc237dde91fdbe979b05ab",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "10c41f0f-5035-49d8-9f35-48d629300240",
            "title" : "2 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "3ba1f281-68f9-f13f-7269-c820e213e3d1",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "91f774e118e1a669195b0c0af5bcc203",
            "target" : "d5737acc-b1a7-ef56-a09f-8996fbae0ebd",
            "title" : "1 insights",
            "width" : 3
          }, {
            "source" : "af2783e3-0ac1-4344-9e8d-72f524ba2080",
            "target" : "91f774e118e1a669195b0c0af5bcc203",
            "title" : "1 insights",
            "width" : 3
          }, 
        ],
      };
      
      
      const tooltip = new G6.Tooltip({
        offsetX: 10,
        offsetY: 10,
        trigger: 'click',
        // the types of items that allow the tooltip show up
        // 允许出现 tooltip 的 item 类型
        itemTypes: ['node', 'edge'],
        // custom the tooltip's content
        // 自定义 tooltip 内容
        getContent: (e) => {
          const outDiv = document.createElement('div');
          outDiv.style.width = 'fit-content';
          //outDiv.style.padding = '0px 0px 20px 0px';
          outDiv.innerHTML = `
            <h4>Custom Content</h4>
            <ul>
              <li>Type: ${e.item.getType()}</li>
            </ul>
            <ul>
              <li>Label: ${e.item.getModel().label || e.item.getModel().id}</li>
            </ul>`;
          return outDiv;
        },
        shouldBegin: (e) => {
          console.log(e.target);
          let res = true;
          switch (e.item.getModel().id) {
            case '1':
              res = false;
              break;
            case '2':
              if (e.target.get('name') === 'text-shape') res = true;
              else res = false;
              break;
            case '3':
              if (e.target.get('name') !== 'text-shape') res = true;
              else res = false;
              break;
            default:
              res = true;
              break;
          }
          return res;
        },
      });


      const container = document.getElementById('g6-container');
      const width = container.scrollWidth;
      const height = container.scrollHeight || 500;
      const graph = new G6.Graph({
        container: 'g6-container',
        width,
        height :700,
        plugins: [tooltip],
        layout: {
          type: 'force',
          preventOverlap: true,
          nodeSize: 60,
          
            linkDistance: (d) => {
            if (d.source.id === '0') {
                return 400;
            }
            return 170;
            },
            nodeStrength: (d) => {
            if (d.isLeaf) {
                return 30;
            }
            return -10;
            },
            edgeStrength: (d) => {
            if (d.source.id === '1' || d.source.id === '5' || d.source.id === '7') {
                return 0.9;
            }
            return 0.1;
            },
        },
        modes: {
          default: ['drag-node'],
        },
        defaultNode: {
          size: 60,
          style: {
            fill: '#fa0b00',
            stroke: '#fa0b00',
            lineWidth: 0.3,
          },
          labelCfg: {
            style: {
              fontSize: 12,
            },
            position: 'bottom',
            offset: 1,
          },
        },
        defaultEdge: {
            type: 'arc',
            /* you can configure the global edge style as following lines */
             style: {
               stroke: '#fa0b00',
               lineWidth: 2,
             },
          },
          nodeStateStyles: {
            selected: {
              stroke: '#fa0b00',
              fill: '#fa0b00',
              opacity: 0.7,
              lineWidth: 3,
            },
          },
          edgeStateStyles: {
            selected: {
              lineWidth: 4,
              stroke: '#fa0b00',
            },
          },
      });
    /*   graph.on('node:mouseenter', (e) => {
        graph.setItemState(e.item, 'active', true);
      });
      
      graph.on('node:mouseleave', (e) => {
        graph.setItemState(e.item, 'active', false);
      }); 
      
      graph.on('nodeselectchange', (e) => {
        console.log(e.selectedItems, e.select);
      });*/

      graph.on('node:click', (evt) => {
        const { item } = evt;
        //graph.setItemState(item, 'selected', true);
        var x = document.getElementById("g6-text-container");
            if (x.style.display === "none") {
                x.style.display = "block";
            } else {
                x.style.display = "none";
            }
            graph.node((node) => {
                return {
                  id: node.id,
                  type: 'rect',
                  style: {
                    fill: 'blue',
                  },
                };
              });
      });
      graph.on('node:dblclick', (evt) => {
        console.log("DBLCLK: "+evt.item.getModel().id)
        const { item } = evt;
        graph.setItemState(item, 'selected', false);
      });
      graph.on('edge:mouseenter', (e) => {
        graph.setItemState(e.item, 'active', true);
      });
      graph.on('edge:mouseleave', (e) => {
        graph.setItemState(e.item, 'active', false);
      });
      

      graph.data(data);
      graph.render();
      
      function refreshDragedNodePosition(e) {
        const model = e.item.get('model');
        model.fx = e.x;
        model.fy = e.y;
      }
      graph.on('node:dragstart', (e) => {
        graph.layout();
        refreshDragedNodePosition(e);
      });
      graph.on('node:drag', (e) => {
        refreshDragedNodePosition(e);
      });
      if (typeof window !== 'undefined')
        window.onresize = () => {
          if (!graph || graph.get('destroyed')) return;
          if (!container || !container.scrollWidth || !container.scrollHeight) return;
          graph.changeSize(container.scrollWidth, container.scrollHeight);
        };
        


/* 
// Connected components
const components = getConnectedComponents(data, false);
components.forEach((component) => {
  console.log(component.map((node) => node.get('id')));
});


// Strongly-connected components
const components2 = getConnectedComponents(data, true);
components2.forEach((component) => {
  console.log(component.map((node) => node.get('id')));
}); */
